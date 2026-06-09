import { getToken } from '@/utils/authority';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import {
  Alert,
  Button,
  Col,
  Form,
  Input,
  message,
  Modal,
  Popconfirm,
  Radio,
  Row,
  Spin,
  Table,
  Upload,
} from 'antd';
import update from 'immutability-helper';
import React from 'react';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
// 将connect导入
import { post } from '@/utils/request';
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

class bannerList extends React.Component {
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
      async () => {
        const res = await post('/guzhe/homeBanner/allList', { bannerType: 0 });
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
            const cloneDeep = [];
            for (let i = 0; i < this.state.updataCloneList.length; i += 1) {
              console.l;
              cloneDeep.push({
                id: this.state.updataCloneList[i].id,
                sort: reverseData[i],
              });
            }
            const res = await post('/guzhe/homeBanner/sort/set', {
              searchIds: cloneDeep.map((res) => res.id),
              bannerType: 0,
            });
            if (res && res.code == 10000) {
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
          this.setState(
            {
              imageUrl: record.banUrl,
            },
            () => {
              this.formRefs.current.setFieldsValue({
                type: record.type,
                url: record.url,
                pic: record.banUrl,
                status: record.status,
                name: record.name,
                nickName: record.nickName,
                sortType: record.sortType,
              });
            },
          );
        } else {
          this.formRefs.current.resetFields();
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
      this.formRefs.current.validateFields().then(async (values) => {
        const res = await post('/guzhe/homeBanner/insert', {
          banUrl: this.state.imageUrl,
          type: values.type,
          url: values.url,
          status: values.status,
          name: values.name,
          nickName: values.nickName,
          sortType: values.sortType,
          bannerType: 0,
        });
        if (res.code == 10000) {
          message.success(res.msg);
          this.setState({
            NewType: false,
          });
          this.getData();
        } else {
          message.info(res?.msg);
        }
      });
    } else {
      this.formRefs.current.validateFields().then(async (values) => {
        const res = await post('/guzhe/homeBanner/update', {
          banUrl: this.state.imageUrl,
          type: values.type,
          url: values.url,
          status: values.status,
          name: values.name,
          nickName: values.nickName,
          sortType: values.sortType,
          id: this.state.edit.id,
          bannerType: 0,
        });
        if (res.code == 10000) {
          message.success(res.msg);
          this.setState({
            NewType: false,
          });
          this.getData();
        } else {
          message.error(res?.msg);
        }
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
    if (info.file.status == 'uploading') {
      this.setState({ loading: true });
      return;
    }
    if (info.file.status == 'done') {
      console.log(info);
      this.setState({
        imageUrl: urlName + info.file.response.data.url,
        loading: false,
      });
    }
  };

  siteFrontReserve = async (v, ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的数据');
    } else {
      const res = await post('/guzhe/homeBanner/status/set', {
        searchIntStatus: v,
        searchIds: ids,
        bannerType: 0,
      });
      if (res && res.code == 10000) {
        this.setState({
          selectedRowKeys: [],
        });
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    }
  };

  //删除
  deletes = async (ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的快捷操作');
    } else {
      const res = await post('/guzhe/homeBanner/delete', {
        bannerType: 0,
        deleteIds: ids,
      });
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.setState({
          selectedRowKeys: [],
        });
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

  render() {
    const {
      gymTypelist = [],
      loading,
      add,
      NewType,
      imageUrl,
      selectedRowKeys,
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
        dataIndex: 'banUrl',
        render: (text, record) => {
          return (
            <>
              <img
                src={text}
                alt=""
                style={{ width: 100, height: 50, objectFit: 'contain' }}
              />
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
        dataIndex: 'nickName',
      },
      {
        title: '权限控制',
        dataIndex: 'sortType',
        render: (text, record) => {
          return (
            <>
              {record.sortType == 0 && <>所有用户</>}
              {record.sortType == 1 && <>仅管理员</>}
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
                {text == 1 ? (
                  <span className="luSe">是</span>
                ) : (
                  <span className="huangse">否</span>
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

              {record.status == 1 ? (
                <a
                  className="mL15"
                  onClick={() => this.siteFrontReserve(0, [record.id])}
                >
                  禁用
                </a>
              ) : (
                <a
                  className="mL15"
                  onClick={() => this.siteFrontReserve(1, [record.id])}
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
      <Spin spinning={this.state.spinning}>
        <div style={{ backgroundColor: '#f0f2f5' }}>
          <div
            style={{ padding: 24, backgroundColor: '#fff', paddingBottom: 0 }}
          >
            <div>
              <Row>
                <Col span={6}>
                  <h3
                    style={{ fontWeight: '600', fontSize: '18px', margin: 0 }}
                  >
                    首页banner图
                  </h3>
                </Col>
                <Col span={18} style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={() => this.showModal(true)}>
                    +新增banner图
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
                          <span style={{ color: 'red' }}>
                            删除的内容不可恢复
                          </span>
                          ，<span style={{ color: '#ccc' }}>确定删除吗？</span>
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
          title={add ? '新增banner图' : '编辑banner图'}
          visible={NewType}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
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
              <Form.Item
                name="pic"
                noStyle
                rules={[{ required: true, message: '请输入' }]}
              >
                <Upload
                  name="file"
                  listType="picture-card"
                  className="avatar-uploader"
                  showUploadList={false}
                  action="/guzhe/file/upload"
                  headers={{ token: getToken() }}
                  beforeUpload={this.beforeUpload}
                  onChange={this.handleChange}
                >
                  {imageUrl ? (
                    <img
                      src={imageUrl}
                      alt="avatar"
                      style={{ width: '100%' }}
                    />
                  ) : (
                    uploadButton
                  )}
                </Upload>
              </Form.Item>
              <span style={{ color: '#ccc' }}>建议尺寸689*156px</span>
            </Form.Item>

            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>名称
                </span>
              }
            >
              <Form.Item
                name="name"
                noStyle
                rules={[{ required: true, message: '请输入!' }]}
              >
                <Input />
              </Form.Item>
              <div style={{ color: '#ccc' }}>名称唯一</div>
            </Form.Item>

            <Form.Item label={<span>别名</span>}>
              <Form.Item
                name="nickName"
                noStyle
                rules={[{ message: '请输入!' }]}
              >
                <Input />
              </Form.Item>
              <div style={{ color: '#ccc' }}>别名唯一</div>
            </Form.Item>

            <Form.Item
              label="权限控制"
              // {...layout}
              name="sortType"
              initialValue={0}
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Radio.Group>
                <Radio value={0}>全部用户</Radio>
                <Radio value={1}>仅管理员</Radio>
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
export default bannerList;
