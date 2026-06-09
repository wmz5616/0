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

// 应用类型
const { Option } = Select;
const { Search } = Input;
const { TextArea } = Input;

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
        //类型
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            id: this.state.id,
            title: this.state.title,
            enable: this.state.enable,
          },
          url: `/api/admin/helper/qa/type/lists`,
          method: 'GET',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              console.log(res.data);
              this.setState({
                gymTypelist: res.data.lists,
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
                id: cloneDeep.map((res) => res.id).join(','),
              },
              url: `/api/admin/helper/qa/type/sort`,
              method: 'POST',
              myData: (res) => {
                if (res && res.code === 200) {
                  message.success(res.message);
                  // this.setState({
                  //   gymList: res.data.lists,
                  // });
                } else {
                  message.error(res.message);
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
  showModal = (add, record) => {
    this.setState(
      {
        NewType: true,
        add: add,
        edit: record,
      },
      () => {
        if (record) {
          console.log(record);
          this.setState(
            {
              addUrl: record.icon,
              imageUrl: record.icon,
            },
            () => {
              this.formRefs.current.setFieldsValue({
                enable: record.enable,
                title: record.title,
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
    console.log(1212);
    const { dispatch } = this.props;
    if (this.state.add) {
      this.formRefs.current.validateFields().then((values) => {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            title: values.title,
            enable: values.enable,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/helper/qa/type/add`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              this.setState({
                NewType: false,
              });
              this.getData();
            } else {
              message.success(res.message);
            }
          },
        });
      });
    } else {
      this.formRefs.current.validateFields().then((values) => {
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            title: values.title,
            enable: values.enable,
            id: this.state.edit.id,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/helper/qa/type/update`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              this.setState({
                NewType: false,
              });
              this.getData();
            } else {
              message.success(res.message);
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
        addUrl: info.file.response.data.uri,
      });

      message.success({ content: '上传成功', duration: 0.7 });
      // Get this url from response in real world.
      this.getBase64(info.file.originFileObj, (imageUrl) =>
        this.setState({
          imageUrl,
          uditUrl: imageUrl,
          loading: false,
        }),
      );
    }
  };

  //查询
  onFinish = (vas) => {
    console.log(vas);
    this.setState(
      {
        id: vas.id,
        title: vas.title,
        enable: vas.enable,
      },
      () => {
        this.getData();
      },
    );
  };

  //重置
  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        id: undefined,
        title: undefined,
        enable: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  siteFrontReserve = (v, ids) => {
    console.log(ids);

    if (ids == undefined) {
      message.error('请选择需要操作的类型');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          enable: v,
          id: ids.join(','),
        },
        url: `/api/admin/helper/qa/type/enable`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            message.success(res.message);
            this.getData();
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  //删除
  deletes = (ids) => {
    if (ids == undefined) {
      message.error('请选择需要操作的类型');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids.join(','),
        },
        url: `/api/admin/helper/qa/type/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            message.success(res.message);
            this.getData();
          } else {
            message.error(res.message);
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

  handleCancels = () => {
    this.setState({
      venue: false,
    });
    this.formRefss.current.resetFields();
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
          if (res.code === 200) {
            console.log(res);
            message.success(res.message);
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
        title: '名称',
        dataIndex: 'title',
      },
      {
        title: '问题数量',
        dataIndex: 'qa_total',
      },
      {
        title: '是否启用',
        dataIndex: 'enable',
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
        dataIndex: 'created_at',
      },
      {
        title: '操作',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record)}>编辑</a>

              {record.enable == 1 ? (
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
      <>
        <div style={{ backgroundColor: '#f0f2f5', paddingBottom: 15 }}>
          <div style={{ padding: 24, backgroundColor: '#fff', paddingBottom: 0 }}>
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="ID" name="id">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="名称" name="title">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="是否启用" name="enable">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>是</Option>
                      <Option value={0}>否</Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={12} style={{textAlign:'right'}}>
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>

                    <Button className="mL15" onClick={this.resets}>
                      重置
                    </Button>
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </div>
        </div>

        <div style={{ textAlign: 'right', padding: 24 }}>
          <span style={{ fontSize: 18, float: 'left' }}>
            <b>常见问题</b>
          </span>

          <Button type="primary" onClick={() => this.showModal(true)}>
            +新增问题类型
          </Button>
          <Button className="mL15 bxluSe" onClick={() => this.siteFrontReserve(1, selectedRowKeys)}>
            启 用
          </Button>
          <Button
            className="mL15 bxHuang"
            onClick={() => this.siteFrontReserve(0, selectedRowKeys)}
          >
            禁 用
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
          style={{ marginBottom: 10,marginTop:24,textAlign:'left' }}
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
          title={add ? '新增问题类型' : '编辑问题类型'}
          visible={NewType}
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          destroyOnClose
          width={600}
          // confirmLoading={confirmLoading}
        >
          <Form ref={this.formRefs} {...layout}>
            <Form.Item label="名称" name="title" rules={[{ required: true, message: '请输入' }]}>
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item
              label="是否启用"
              // {...layout}
              name="enable"
              initialValue={1}
            >
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={2}>否</Radio>
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
export default connect((allModels) => ({}))(DataConnection);
