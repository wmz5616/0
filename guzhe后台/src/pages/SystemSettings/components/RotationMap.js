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
// 应用类型

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
    xxx: 1,
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
        //类型
        const res = await post(`/guzhe/homePage/banner/allList`);
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          console.log(res.data);
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
            const res = await post(`/guzhe/homePage/banner/sort/set`, {
              searchIds: cloneDeep.map(i=>i.id),
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
        addUrl: undefined,
        imageUrl: undefined,
        xxx: 1,
      },
      () => {
        if (record) {
          console.log(record);
          this.setState(
            {
              addUrl: record.pic,
              imageUrl: record.pic,
              xxx: record.type,
              id: record.id,
            },
            () => {
              setTimeout(() => {
                this.formRefs.current?.setFieldsValue({
                  pic: record.pic,
                  status: record.status,
                  link: record.url,
                  link_type: record.type,
                });
              }, 0);
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
    // 获取表单数据
    console.log(this.formRefs.current.getFieldsValue());
    this.formRefs.current.validateFields().then(async (values) => {
      const params = {
        pic: this.state.imageUrl,
        type: values.link_type,
        url: values.link ? values.link : '',
        status: values.enable,
      };
      if (!this.state.add) {
        params.id = this.state.id;
      }
      const res = await post(
        `/guzhe/homePage/banner/${this.state.add ? 'insert' : 'update'}`,
        {
          ...params,
        },
      );
      if (res.code == 10000) {
        message.success(res.msg);
        this.setState({
          NewType: false,
          addUrl: undefined,
          imageUrl: undefined,
        });
        this.getData();
      } else {
        message.success(res?.msg);
      }
    });
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
      const { response = {} } = info.file;
      this.setState({
        imageUrl: urlName + response.data.url,
      });

      message.success({ content: '上传成功', duration: 0.7 });
      this.getBase64(info.file.originFileObj, (imageUrl) =>
        this.setState({
          // imageUrl,
          uditUrl: imageUrl,
          loading: false,
        }),
      );
    }
  };

  operationStatus = async (e, id) => {
    if (id.length == 0) {
      message.info('请选择需要操作的数据');
      return;
    }
    const res = await post(`/guzhe/homePage/banner/status/set`, {
      searchIntStatus: e,
      searchIds: id,
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
  };

  //删除
  deletes = async (ids) => {
    if (ids.length == 0) {
      message.info('请选择需要操作的图片');
    } else {
      const res = await post(`/guzhe/homePage/banner/delete`, {
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

  onChange = (e) => {
    this.setState({
      xxx: e.target.value,
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
        title: '图片',
        dataIndex: 'pic',
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
        title: '链接类型',
        dataIndex: 'type',
        render: (text, record) => {
          return (
            <>
              {record.type == 0 && <>无</>}
              {record.type == 2 && <>小程序页面</>}
              {record.type == 1 && <>H5网页</>}
            </>
          );
        },
      },
      {
        title: '链接地址',
        dataIndex: 'url',
        render: (res, record) => <div>{record.type != 0 && res}</div>,
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
                  onClick={() => this.operationStatus(0, [record.id])}
                >
                  禁用
                </a>
              ) : (
                <a
                  className="mL15"
                  onClick={() => this.operationStatus(1, [record.id])}
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
        <div style={{ paddingBottom: 15 }}>
          <div
            style={{ padding: 24, backgroundColor: '#fff', paddingBottom: 0 }}
          >
            <Row>
              <Col span={6}>
                <h3 style={{ fontWeight: '600', fontSize: '18px', margin: 0 }}>
                  首页轮播图
                </h3>
              </Col>
              <Col span={18} style={{ textAlign: 'right' }}>
                <Button type="primary" onClick={() => this.showModal(true)}>
                  +新增图片
                </Button>
                <Button
                  className="mL15 bxluSe"
                  onClick={() =>
                    this.operationStatus(1, this.state.selectedRowKeys)
                  }
                >
                  启用
                </Button>
                <Button
                  className="mL15 bxHuang"
                  onClick={() =>
                    this.operationStatus(0, this.state.selectedRowKeys)
                  }
                >
                  禁用
                </Button>

                <Popconfirm
                  title={
                    <>
                      <div>删除提示</div>
                      <div>
                        <span style={{ color: 'red' }}>删除的内容不可恢复</span>
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
          </div>{' '}
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
                  <span style={{ color: 'red' }}>*</span>图片
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
                      style={{
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover',
                      }}
                    />
                  ) : (
                    uploadButton
                  )}
                </Upload>
              </Form.Item>
              <span style={{ color: '#ccc' }}>建议尺寸690*246px</span>
            </Form.Item>

            <Form.Item
              label="链接类型"
              // {...layout}
              name="link_type"
              rules={[{ required: true, message: '请选择' }]}
            >
              <Radio.Group onChange={this.onChange}>
                <Radio value={0}>无</Radio>
                <Radio value={2}>小程序页面</Radio>
                <Radio value={1}>H5网页</Radio>
              </Radio.Group>
            </Form.Item>
            {this.state.xxx != 0 && (
              <Form.Item
                label="链接地址"
                name="link"
                rules={[{ required: true, message: '请输入' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
            )}

            <Form.Item
              label="是否启用"
              // {...layout}
              name="enable"
              initialValue={1}
              rules={[{ required: true, message: '请选择' }]}
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
export default DataConnection;
