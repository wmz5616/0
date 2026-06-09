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
  Spin,
  Alert,
  Modal,
  Upload,
  Radio,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import { getToken } from '@/utils/authority';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
// 将connect导入
import { connect } from 'umi';
import { urlName } from '@/utils/utils';

const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
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

class DataConnection extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  formRefss = React.createRef();
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
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {},
          url: `/ddql/homePage/quickEntry/allList`,
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
              url: `/ddql/homePage/quickEntry/sort/set`,
              method: 'POST',
              myData: (res) => {
                if (res && res.code === 10000) {
                  message.success(res.msg);
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

  // 添加
  showModal = (add, record) => {
    this.setState(
      {
        NewType: true,
        add: add,
        edit: record,
        imageUrl: undefined,
      },
      () => {
        if (record) {
          console.log(record);
          this.setState(
            {
              imageUrl: record.pic,
            },
            () => {
              this.formRefs.current.setFieldsValue({
                type: record.type,
                url: record.url,
                pic: record.pic,
                status: record.status,
                name: record.name,
                otherName: record.otherName,
                permission: record.permission,
              });
            },
          );
        }
      },
    );
  };

  handleCancel = () => {
    this.setState({
      NewType: false,
    });
  };

  handleOk = () => {
    const { dispatch } = this.props;
    if (this.state.add) {
      this.formRefs.current.validateFields().then((values) => {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            pic: this.state.imageUrl,
            type: values.type,
            url: values.url,
            status: values.status,
            name: values.name,
            otherName: values.otherName,
            permission: values.permission,
          },
          method: 'POST',
          url: `/ddql/homePage/quickEntry/insert`,
          myData: (res) => {
            if (res.code === 10000) {
              message.success(res.msg);
              this.setState({
                NewType: false,
              });
              this.getData();
            } else {
              message.info(res.msg);
            }
          },
        });
      });
    } else {
      this.formRefs.current.validateFields().then((values) => {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            pic: this.state.imageUrl,
            type: values.type,
            url: values.link,
            status: values.status,
            name: values.name,
            otherName: values.otherName,
            permission: values.permission,
            id: this.state.edit.id,
          },
          method: 'POST',
          url: `/ddql/homePage/quickEntry/update`,
          myData: (res) => {
            if (res.code === 10000) {
              message.success(res.msg);
              this.setState({
                NewType: false,
              });
              this.getData();
            } else {
              message.success(res.msg);
            }
          },
        });
      });
    }
  };

  beforeUpload = (file) => {
    const isJpgOrPng = file.name.indexOf('jfif') == -1;
    console.log(isJpgOrPng);
    if (!isJpgOrPng) {
      message.error('不符合类型文件');
    }

    return isJpgOrPng && true;
  };

  getBase64 = (img, callback) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result));
    reader.readAsDataURL(img);
  };

  handleChange = (info) => {
    if (info.file.status === 'uploading') {
      this.setState({ loading: true });
      return;
    }
    if (info.file.status === 'done') {
      console.log(info);
      this.setState({
        imageUrl: urlName + info.file.response.data.url,
        loading: false,
      });
    }
  };

  siteFrontReserve = (v, ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的数据');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          searchIntStatus: v,
          searchIds: ids,
        },
        url: `/ddql/homePage/quickEntry/status/set`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            this.setState({
              selectedRowKeys: [],
            });
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

  //删除
  deletes = (ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的快捷操作');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          deleteIds: ids,
        },
        url: `/ddql/homePage/quickEntry/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.setState({
              selectedRowKeys: [],
            });
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

  //设置场馆
  Venue = (res, typeId) => {
    this.setState({
      venue: true,
      venueId: res,
      typeId: typeId,
    });
  };

  handleOks = () => {
    console.log(this.state.typeId);
    const { dispatch } = this.props;
    this.formRefss.current.validateFields().then((values) => {
      console.log(123);
      dispatch({
        type: 'myModel/getSetData',
        payload: {
          master_gym: values.master_gym,
          id: this.state.typeId,
        },
        // dataName: 'developerListData',
        method: 'POST',
        url: `/api/admin/gym_type/bind_master_gym`,
        myData: (res) => {
          if (res.code === 10000) {
            console.log(res);
            message.success(res.msg);
            this.setState({
              venue: false,
            });
            this.getData();
          }
        },
      });
    });
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

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '图标',
        dataIndex: 'pic',
        render: (text, record) => {
          return (
            <>
              <img src={text} alt="" style={{ width: 100, height: 50, objectFit: 'contain' }} />
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
        title: '权限控制',
        dataIndex: 'permission',
        render: (text, record) => {
          return (
            <>
              {record.permission == 0 && <>所有用户</>}
              {record.permission == 1 && <>仅管理员</>}
            </>
          );
        },
      },

      {
        title: '链接类型',
        dataIndex: 'type',
        render: (text, record) => {
          return (
            <>
              {record.type == 0 && <>小程序页面</>}
              {record.type == 1 && <>H5网页</>}
            </>
          );
        },
      },
      {
        title: '链接地址',
        dataIndex: 'url',
      },

      {
        title: '排序',
        dataIndex: 'sort',
      },

      {
        title: '是否启用',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <span>
                {text == 1 ? <span className="luSe">是</span> : <span className="huangse">否</span>}
              </span>
            </div>
          );
        },
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
      },
      {
        title: '操作',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record)}>编辑</a>

              {record.status == 1 ? (
                <a className="mL15" onClick={() => this.siteFrontReserve(0, [record.id])}>
                  禁用
                </a>
              ) : (
                <a className="mL15" onClick={() => this.siteFrontReserve(1, [record.id])}>
                  启用
                </a>
              )}
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

    return (
      <Spin spinning={this.state.spinning}>
        <div style={{ backgroundColor: '#f0f2f5', paddingBottom: 15 }}>
          <div style={{ padding: 24, backgroundColor: '#fff', paddingBottom: 0 }}>
            <div>
              <Row>
                <Col span={6}>
                  <h3 style={{ fontWeight: '600', fontSize: '18px', margin: 0 }}>首页快捷入口</h3>
                </Col>
                <Col span={18} style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={() => this.showModal(true)}>
                    +新增快捷操作
                  </Button>
                  <Button
                    className="mL15 bxluSe"
                    onClick={() => this.siteFrontReserve(1, selectedRowKeys)}
                  >
                    启用
                  </Button>
                  <Button
                    className="mL15 bxHuang"
                    onClick={() => this.siteFrontReserve(0, selectedRowKeys)}
                  >
                    禁用
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
                </Col>
              </Row>

              <Alert
                message="按住鼠标拖拽可调整展示顺序"
                type="warning"
                showIcon
                style={{ marginBottom: 10, marginTop: 20 }}
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
          </div>
        </div>
        <Modal
          title={add ? '新增图片' : '编辑图片'}
          visible={NewType}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          destroyOnClose
          width={600}
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
              <Form.Item name="pic" noStyle rules={[{ required: true, message: '请输入' }]}>
                <Upload
                  name="file"
                  listType="picture-card"
                  className="avatar-uploader"
                  showUploadList={false}
                  action="/ddql/file/upload"
                  headers={{ token: getToken() }}
                  beforeUpload={this.beforeUpload}
                  onChange={this.handleChange}
                >
                  {imageUrl ? (
                    <img src={imageUrl} alt="avatar" style={{ width: '100%' }} />
                  ) : (
                    uploadButton
                  )}
                </Upload>
              </Form.Item>
              <span style={{ color: '#ccc' }}>建议尺寸100*100 px</span>
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>名称
                </span>
              }
            >
              <Form.Item name="name" noStyle rules={[{ required: true, message: '请输入!' }]}>
                <Input />
              </Form.Item>
              <div style={{ color: '#ccc' }}>名称唯一</div>
            </Form.Item>

            <Form.Item label={<span>别名</span>}>
              <Form.Item name="otherName" noStyle rules={[{ message: '请输入!' }]}>
                <Input />
              </Form.Item>
              <div style={{ color: '#ccc' }}>别名唯一</div>
            </Form.Item>

            <Form.Item
              label="权限控制"
              // {...layout}
              name="permission"
              initialValue={1}
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Radio.Group>
                <Radio value={1}>全部用户</Radio>
                <Radio value={2}>仅管理员</Radio>
              </Radio.Group>
            </Form.Item>

            <Form.Item
              label="链接类型"
              // {...layout}
              name="type"
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Radio.Group>
                <Radio value={0}>小程序页面</Radio>
                <Radio value={1}>H5网页</Radio>
              </Radio.Group>
            </Form.Item>

            <Form.Item
              label="链接地址"
              // {...layout}
              name="url"
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item
              label="是否启用"
              // {...layout}
              name="status"
              initialValue={1}
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            </Form.Item>
          </Form>
        </Modal>
      </Spin>
    );
  }
}

// 7
// 绑定到本页面，此处就可以拿到请求的值，
// allModels所有models集合，mapping是具体的model的命名空间，mappingData命名空间里面你想取的值
export default connect((allModels) => ({}))(DataConnection);
