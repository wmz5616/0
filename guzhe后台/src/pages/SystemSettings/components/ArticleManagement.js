import { getToken } from '@/utils/authority';
import { post } from '@/utils/request';
import { urlName } from '@/utils/utils';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import {
  Alert,
  Button,
  Form,
  Input,
  message,
  Modal,
  Popconfirm,
  Radio,
  Table,
  Upload,
} from 'antd';
import update from 'immutability-helper';
import React from 'react';
import CKEditor from 'react-ckeditor-wrapper';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
// 将connect导入
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
    const {
      isOver,
      connectDragSource,
      connectDropTarget,
      moveRow,
      ...restProps
    } = this.props;
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
      connectDropTarget(
        <tr {...restProps} className={className} style={style} />,
      ),
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
    if (dragIndex == hoverIndex) {
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
  state = {
    NewType: false,
    RecommendedSettings: false,
    confirmLoading: false,
    spinning: false,
    loading: false,
    imageUrl: '',
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      async () => {
        //列表
        const res = await post(`/guzhe/article/select`);
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          this.setState({
            gymTypelist: res.data,
          });
        } else {
          message.error(res?.msg);
        }
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
          async () => {
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
            const res = await post(`/guzhe/article/sort/set`, {
              ids: cloneDeep.map((res) => res.id),
            });
            if (res && res.code ==10000) {
              message.success(res.msg);
              this.getData();
            } else {
              message.error(res?.msg);
            }
          },
        );
      },
    );
  };

  // 添加设置
  showModal = (add, record) => {
    this.setState(
      {
        NewType: true,
        add: add,
        edit: record,
      },
      () => {
        if (record) {
          this.setState(
            {
              content: record.content,
            },
          );
          setTimeout(() => {
            if (this.formRefs.current) {
              this.formRefs.current.setFieldsValue({
                status: record.status,
                title: record.title,
                content: record.content,
              });
            }
          }, 100);
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

  handleOk = async () => {
    const { dispatch } = this.props;
    if (this.state.add) {
      this.formRefs.current.validateFields().then(async (values) => {
        const res = await post(`/guzhe/article/add`, {
          title: values.title,
          status: values.status,
          content: this.state.content,
        });
        if (res.code == 10000) {
          message.success(res.msg);
          this.setState({
            NewType: false,
            content: '',
          });
          this.getData();
        } else {
          message.error(res?.msg);
        }
      });
    } else {
      this.formRefs.current.validateFields().then(async (values) => {
        const res = await post(`/guzhe/article/update`, {
          title: values.title,
          status: values.status,
          id: this.state.edit.id,
          content: this.state.content,
        });
        if (res.code == 10000) {
          message.success(res.msg);
          this.setState({
            NewType: false,
            content: '',
          });
          this.getData();
        } else {
          message.error(res?.msg);
        }
      });
    }
  };

  siteFrontReserve = async (v, ids) => {
    const res = await post(`/guzhe/article/update`, {
      status: v,
      title: ids.title,
      content: ids.content,
      id: ids.id,
    });
    if (res && res.code == 10000) {
      message.success(res.msg);
      this.getData();
    } else {
      message.error(res?.msg);
    }
  };

  //删除
  deletes = async (ids) => {
    if (ids.length == 0) {
      message.error('请选择需要删除的数据');
    } else {
      const res = await post(`/guzhe/article/delete`, {
        deleteIds: ids,
      });
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    }
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  updateContent = (value, index) => {
    this.setState({
      content: value,
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

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/guzhe/file/upload',

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
          if (info.file.status == 'done') {
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
        title: '标题',
        dataIndex: 'title',
      },
      {
        title: '是否启用',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <span>
                {text == 0 ? (
                  <span className="luSe">是</span>
                ) : (
                  <span className="red">否</span>
                )}
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

              {record.status == 0 ? (
                <a
                  className="mL15"
                  onClick={() => this.siteFrontReserve(1, record)}
                >
                  禁用
                </a>
              ) : (
                <a
                  className="mL15"
                  onClick={() => this.siteFrontReserve(0, record)}
                >
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
      <>
        <div style={{ textAlign: 'right', padding: 24 }}>
          <span style={{ fontSize: 18, float: 'left' }}>
            <b>文章管理</b>
          </span>

          <Button type="primary" onClick={() => this.showModal(true)}>
            +新增文章
          </Button>
          {/* <Button className="mL15 bxluSe" onClick={() => this.siteFrontReserve(1, selectedRowKeys)}>
            启 用
          </Button>asd
          <Button
            className="mL15 bxHuang"
            onClick={() => this.siteFrontReserve(2, selectedRowKeys)}
          >
            禁 用
          </Button> */}

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
          title={add ? '新增文章' : '编辑文章'}
          visible={NewType}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={800}
          // confirmLoading={confirmLoading}
        >
          <Form ref={this.formRefs} {...layout}>
            <Form.Item
              label="标题"
              name="title"
              rules={[{ required: true, message: '请输入' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>正文
                </span>
              }
              rules={[{ required: true, message: '请输入!' }]}
            >
              <div style={{ position: 'relative' }}>
                <Upload
                  showUploadList={false}
                  accept={'image/*'}
                  headers={{ token: getToken() }}
                  {...uploadProps(1)}
                >
                  <div className="zxc" />
                </Upload>
                <CKEditor
                  ref={(ckeditor) => {
                    this.ckeditor = ckeditor;
                  }}
                  value={this.state.content}
                  config={{
                    toolbar: [
                      {
                        name: 'clipboard',
                        items: [
                          'Cut',
                          'Copy',
                          'Paste',
                          'PasteText',
                          'PasteFromWord',
                          '-',
                        ],
                      },
                      {
                        name: 'basicstyles',
                        items: [
                          'Bold',
                          'Italic',
                          'Underline',
                          '-',
                          'CopyFormatting',
                        ],
                      },
                      {
                        name: 'paragraph',
                        items: [
                          'NumberedList',
                          'BulletedList',
                          '-',
                          'Outdent',
                          'Indent',
                          '-',
                          'JustifyLeft',
                          'JustifyCenter',
                          'JustifyRight',
                          'JustifyBlock',
                          '-',
                        ],
                      },
                      { name: 'links', items: ['Link', 'Unlink'] },
                      { name: 'insert', items: ['Image', 'Table'] },
                      { name: 'styles', items: ['Font', 'FontSize'] },
                      { name: 'colors', items: ['TextColor', 'BGColor'] },
                      { name: 'tools', items: ['Maximize'] },
                    ],
                    extraPlugins: 'placeholder',
                    height: 250,
                    // uploadUrl: '/home/media/upload',
                    removeDialogTabs: 'image:advanced;link:advanced',
                  }}
                  onChange={this.updateContent}
                />
              </div>
            </Form.Item>

            <Form.Item
              label="是否启用"
              // {...layout}
              name="status"
              initialValue={0}
            >
              <Radio.Group>
                <Radio value={0}>是</Radio>
                <Radio value={1}>否</Radio>
              </Radio.Group>
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
export default DataConnection;
