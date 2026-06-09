import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Select,
  Input,
  Table,
  Row,
  Col,
  Form,
  message,
  Popconfirm,
  Switch,
  Alert,
  Modal,
  Upload,
  Radio,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import CKEditor from 'react-ckeditor-wrapper';
import update from 'immutability-helper';
import { getToken } from '@/utils/authority';
import { urlName } from '@/utils/utils';
import ImgCrop from 'antd-img-crop';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
// 将connect导入
import { connect } from 'umi';

// 应用类型
const { Option } = Select;
const { Search } = Input;
const { TextArea } = Input;

const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};

let dragingIndex = -1;
class BodyRow extends React.Component {
  state = {
    updataCloneList: [],
  };

  render() {
    const { isOver, connectDragSource, connectDropTarget, moveRow, ...restProps } = this.props;
    const style = { ...restProps.style, cursor: 'move' };

    let { className } = restProps;
    if (isOver) {
      if (restProps.index > dragingIndex) {
        className += ' drop-over-downward';
      }
      if (restProps.index < dragingIndex) {
        className += ' drop-over-upward';
      }
    }

    return connectDragSource(
      connectDropTarget(<tr {...restProps} className={className} style={style} />),
    );
  }
}

const rowSource = {
  beginDrag(props) {
    dragingIndex = props.index;
    return {
      index: props.index,
    };
  },
};

const rowTarget = {
  drop(props, monitor) {
    const dragIndex = monitor.getItem().index;
    const hoverIndex = props.index;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return;
    }

    // Time to actually perform the action
    props.moveRow(dragIndex, hoverIndex);

    // Note: we're mutating the monitor item here!
    // Generally it's better to avoid mutations,
    // but it's good here for the sake of performance
    // to avoid expensive index searches.
    monitor.getItem().index = hoverIndex;
  },
};

const DragableBodyRow = DropTarget('row', rowTarget, (connect, monitor) => ({
  connectDropTarget: connect.dropTarget(),
  isOver: monitor.isOver(),
}))(
  DragSource('row', rowSource, (connect) => ({
    connectDragSource: connect.dragSource(),
  }))(BodyRow),
);

class DakaType extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    NewType: false,
    RecommendedSettings: false,
    confirmLoading: false,
    spinning: false,
    loading: false,
    imageUrl: '',
    selectedRowKeys: [],
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        //列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {},
          url: `/ddql/checkInType/getCheckInTypeList`,
          method: 'POST',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 10000) {
              this.setState({
                gymTypelist: res.data,
              });
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { gymTypelist } = this.state;
    const dragRow = gymTypelist[dragIndex];
    console.log(hoverIndex);

    this.setState(
      update(this.state, {
        gymTypelist: {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        },
      }),
      () => {
        this.setState(
          {
            updataCloneList: this.state.gymTypelist,
          },
          () => {
            const reverseData = [];
            for (let i = 0; i < this.state.updataCloneList.length; i += 1) {
              reverseData.push(i + 1);
            }
            reverseData.reverse();
            console.log(reverseData);
            const cloneDeep = [];
            for (let i = 0; i < this.state.updataCloneList.length; i += 1) {
              console.l;
              cloneDeep.push({
                id: this.state.updataCloneList[i].id,
                sort: reverseData[i],
              });
            }
            const { dispatch } = this.props;
            console.log(cloneDeep);
            this.props.dispatch({
              type: 'myModel/getSetData',
              payload: {
                searchIds: cloneDeep.map((res) => res.id),
              },
              url: `/ddql/checkInType/sort/set`,
              method: 'POST',
              myData: (res) => {
                if (res && res.code === 10000) {
                  message.success(res.msg);
                  this.getData();
                  // this.setState({
                  //   gymList: res.data.lists,
                  // });
                } else {
                  message.error(res.msg);
                  // this.setState({ isSelectForm: true });
                }
              },
            });
          },
        );
      },
    );
  };

  // sort_data:[
  //   {
  //     id:dragRow.id,
  //     sort:hoverIndex
  //   }
  // ]

  // 添加设置
  showModal = (add, record, id) => {
    this.setState(
      {
        NewType: true,
        isAdd: add,
        edit: record,
        id,
        imageUrl:''
      },
      () => {
        if (record) {
          setTimeout((_) => {
            this.setState({
              imageUrl: record.images,
            });
            this.formRefs.current.setFieldsValue({
              name: record.name,
              otherName: record.otherName,
              instruction: record.instruction,
              status: record.status,
            });
          }, 300);
        }
      },
    );
  };

  handleCancel = () => {
    this.setState({
      NewType: false,
      content: '',
    });
  };

  handleOk = () => {
    const { dispatch } = this.props;
    this.formRefs.current.validateFields().then((values) => {
      const params = {
        images: [this.state.imageUrl],
        name: values.name,
        otherName: values.otherName,
        instruction: values.instruction,
        status: values.status,
      };
      if (!this.state.isAdd) {
        params.id = this.state.id;
      }
      dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        method: 'POST',
        url: this.state.isAdd ? `/ddql/checkInType/add` : `/ddql/checkInType/update`,
        myData: (res) => {
          if (res.code === 10000) {
            message.success(res.msg);
            this.setState({
              NewType: false,
              content: '',
            });
            this.getData();
          } else {
            message.error(res.msg);
          }
        },
      });
    });
  };

  front = (v, values) => {
    const params = {
      images: [values.images],
      name: values.name,
      otherName: values.otherName,
      instruction: values.instruction,
      id: values.id,
      status: v ? 0 : 1,
    };
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      method: 'POST',
      url: `/ddql/checkInType/update`,
      myData: (res) => {
        if (res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  //删除
  deletes = (ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的数据');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          deleteIds: ids,
        },
        url: `/ddql/checkInType/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.getData();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };

  handleChange = (info) => {
    if (info.file.status === 'uploading') {
      this.setState({ loading: true });
      return;
    }
    if (info.file.status === 'done') {
      const { response = {} } = info.file;
      if (response.code == 10000) {
        this.setState({
          imageUrl: urlName + response.data.url,
        });
        message.success({ content: '上传成功', duration: 0.7 });
      } else {
        message.info(response.msg);
      }
    }
  };

  render() {
    const {
      gymTypelist = [],
      loading,
      add,
      NewType,
      imageUrl,
      selectedRowKeys,
      list = [],
    } = this.state;
    const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
    );

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',

        onChange: (info) => {
          const fileType = [
            'doc',
            'txt',
            'pdf',
            'zip',
            'rar',
            'xls',
            'xlsx',
            'docs',
            'pptx',
            'ppt',
          ];
          if (info.file.status === 'done') {
            console.log(info.file);
            const url = urlName + info.file.response.data.url;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', url);

            ckeditor.instance.insertElement(ele);
            // }
          }
        },
      };
    };

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '图标',
        dataIndex: 'images',
        render: (text, record) => {
          return (
            <>
              <img src={text} alt="" style={{ width: 103.5, height: 37.5, objectFit: 'contain' }} />
            </>
          );
        },
      },
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '别名',
        dataIndex: 'otherName',
      },
      {
        title: '启用状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <Switch
                checkedChildren="开启"
                unCheckedChildren="关闭"
                checked={!text}
                onChange={(value) => {
                  this.front(value, record);
                }}
              />
            </div>
          );
        },
      },
      {
        title: '说明',
        dataIndex: 'instruction',
      },
      {
        title: '操作',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record, record.id)}>编辑</a>
              <Popconfirm
                title={
                  <>
                    <div>删除提示</div>
                    <div>
                      <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                      <span style={{ color: '#ccc' }}>确定删除吗？</span>
                    </div>
                  </>
                }
                onConfirm={() => this.deletes([record.id])}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
              >
                <span className="mL15 red">删除</span>
              </Popconfirm>
            </div>
          );
        },
      },
    ];
    const props = {
      grid: false,
      width: 316,
      height: 246,
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 600,
    };
    return (
      <>
        <div
          style={{
            textAlign: 'right',
            padding: 24,
            backgroundColor: '#fff',
            minHeight: window.innerHeight - 200,
          }}
        >
          <span style={{ fontSize: 18, float: 'left' }}>
            <b>打卡类型</b>
          </span>
          <Button type="primary" onClick={() => this.showModal(true)}>
            +新增
          </Button>
          <Popconfirm
            title={
              <>
                <div>删除提示</div>
                <div>
                  <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                  <span style={{ color: '#ccc' }}>确定删除吗？</span>
                </div>
              </>
            }
            onConfirm={() => this.deletes(selectedRowKeys)}
            // onCancel={cancel}
            okText="是"
            cancelText="否"
          >
            <Button danger className="mL15">
              删除
            </Button>
          </Popconfirm>

          <Alert
            message="按住鼠标拖拽可调整展示顺序"
            type="warning"
            showIcon
            style={{ marginBottom: 10, marginTop: 24, textAlign: 'left' }}
          />

          <DndProvider backend={HTML5Backend}>
            <Table
              columns={columns}
              rowKey="id"
              rowSelection={rowSelection}
              dataSource={gymTypelist}
              components={this.components}
              onRow={(record, index) => ({
                index,
                moveRow: this.moveRow,
              })}
              pagination={false}
            />
          </DndProvider>
        </div>
        <Modal
          title={add ? '新增打卡类型' : '编辑打卡类型'}
          open={NewType}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          destroyOnClose
          width={800}
          // confirmLoading={confirmLoading}
        >
          <Form ref={this.formRefs} {...layout}>
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>图标
                </span>
              }
            >
              <Form.Item name="logo" noStyle rules={[{ required: false, message: '请输入!' }]}>
                <ImgCrop {...props}>
                  <Upload
                    name="file"
                    listType="picture-card"
                    className="avatar-uploader"
                    showUploadList={false}
                    action="/ddql/file/upload"
                    headers={{ token: localStorage.getItem('token') }}
                    beforeUpload={this.beforeUpload}
                    onChange={this.handleChange}
                  >
                    {imageUrl ? (
                      <img src={imageUrl} alt="avatar" style={{ width: '100%' }} />
                    ) : (
                      uploadButton
                    )}
                  </Upload>
                </ImgCrop>
              </Form.Item>
              <div style={{ color: '#ccc' }}>支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</div>
            </Form.Item>

            <Form.Item label="名称" name="name" rules={[{ required: true, message: '请输入' }]}>
              <Input placeholder="请输入" />
            </Form.Item>
            <Form.Item
              label="别名"
              name="otherName"
              rules={[{ required: false, message: '请输入' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>
            {!this.props.isAdd && (
              <Form.Item
                rules={[{ required: true }]}
                label="是否启用"
                // {...layout}
                name="status"
              >
                <Radio.Group>
                  <Radio value={0}>是</Radio>
                  <Radio value={1}>否</Radio>
                </Radio.Group>
              </Form.Item>
            )}
            <Form.Item
              label="说明"
              name="instruction"
              rules={[{ required: false, message: '请输入' }]}
            >
              <Input.TextArea placeholder="请输入" />
            </Form.Item>
          </Form>
        </Modal>
        {/* </div>
        </div> */}
      </>
    );
  }
}

// 7
// 绑定到本页面，此处就可以拿到请求的值，
// allModels所有models集合，mapping是具体的model的命名空间，mappingData命名空间里面你想取的值
export default connect((allModels) => ({}))(DakaType);
