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
  Switch
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
    fileList: [],
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
            name: this.state.name,
            alias: this.state.alias,
            is_show: this.state.is_show,
            intro: this.state.intro,
          },
          url: `/api/admin/gym_service/lists`,
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
              url: `/api/admin/gym_service/sort`,
              method: 'POST',
              myData: (res) => {
                if (res && res.code === 200) {
                  message.success(res.message);
                  this.setState({
                    gymList: res.data.lists,
                  });
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
        addUrl: undefined,
        imageUrl: undefined,
      },
      () => {
        if (record) {
          this.setState(
            {
              addUrl: record.icon,
              imageUrl: record.icon,
            },
            () => {
              const xx = record.icon ? record.icon.split(',') : [];
              const cloneCommunities = [];
              xx.map((resd, index) => {
                cloneCommunities.push({
                  response: {
                    data: {
                      uri: resd,
                      code: 200,
                    },
                  },
                  url: resd,
                  uid: (index + 1).toString(),
                });
              });
              this.setState({
                fileList: cloneCommunities,
              })

              this.formRefs.current.setFieldsValue({
                icon: cloneCommunities,
                name: record.name,
                alias: record.alias,
                intro: record.intro,
                is_show: record.is_show,
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
      fileList:[],
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
            name: values.name,
            icon: this.state.fileList.map((res) => res.response.data.uri).join(','),
            alias: values.alias,
            is_show: values.is_show,
            intro: values.intro,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/gym_service/add`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              this.setState({
                NewType: false,
                fileList:[],
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
            name: values.name,
            icon: this.state.fileList.map((res) => res.response.data.uri).join(','),
            alias: values.alias,
            is_show: values.is_show,
            intro: values.intro,
            id: this.state.edit.id,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/gym_service/update`,
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

  onChange = ({ fileList }) => {
    // setFileList(newFileList);
    // console.log(fileList)

    console.log(fileList);
    this.setState({
      fileList: fileList,
      // cover:newFileList.map(res => res.response.data.uri),
    });
  };

  
  onPreview = async (file) => {
    let src = file.url;

    if (!src) {
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);

        reader.onload = () => resolve(reader.result);
      });
    }

    const image = new Image();
    image.src = src;
    const imgWindow = window.open(src);
    imgWindow?.document.write(image.outerHTML);
  };


  //查询
  onFinish = (vas) => {
    console.log(vas);
    this.setState(
      {
        id: vas.id,
        name: vas.name,
        alias: vas.alias,
        intro: vas.intro,
        is_show: vas.is_show,
        selectedRowKeys:[]
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
        name: undefined,
        alias: undefined,
        intro: undefined,
        is_show: undefined,
        selectedRowKeys:[]
      },
      () => {
        this.getData();
      },
    );
  };

  siteFrontReserve = (v, ids) => {
    console.log(v);

    if (ids == undefined) {
      message.error('请选择需要操作的类型');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          show: v,
          id: ids.join(','),
        },
        url: `/api/admin/gym_service/show`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            message.success(res.message);
            this.setState({
              selectedRowKeys:[]
            })
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
        url: `/api/admin/gym_service/delete`,
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



  handleCancels = () => {
    this.setState({
      venue: false,
    });
    this.formRefss.current.resetFields();
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
        title: <div style={{textAlign:'center'}}>图标</div>,
        dataIndex: 'icon',
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
        dataIndex: 'alias',
      },
      {
        title: '说明',
        dataIndex: 'intro',
      },
      {
        title: '启用状态',
        dataIndex: 'is_show',
        render: (text, record) => {
          return (
            <div>
              <Switch
                checkedChildren="开启"
                unCheckedChildren="关闭"
                checked={text}
                onChange={(value) => this.siteFrontReserve(value, [record.id])}
              />
            </div>
          );
        },
      },
      {
        title: '场所数量',
        dataIndex: 'stadium_total',
      },
      {
        title: '创建时间',
        dataIndex: 'updated_at',
      },
      {
        title: '操作',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record)}>编辑</a>
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
        <PageContainer
          header={{
            title: ``,
          }}
        >
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="ID" name="id">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="名称" name="name">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="说明" name="intro">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="是否启用" name="is_show">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>是</Option>
                      <Option value={0}>否</Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={8} style={{textAlign:'right'}}>
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

          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row>
              <Col span={12}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>场所服务</h1>
              </Col>
              <Col span={12} style={{textAlign:'right'}}>
                <Button type="primary" onClick={() => this.showModal(true)}>
                  +新增场所服务
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

          <Modal
            title={add ? '新增场所服务' : '编辑场所服务'}
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
                <Form.Item name="icon" noStyle rules={[{ required: true, message: '请输入' }]}>
                <Upload
                    action="/ddql/file/upload"
                    listType="picture-card"
                    fileList={this.state.fileList}
                    onChange={this.onChange}
                    onPreview={this.onPreview}
                  >
                    {this.state.fileList.length < 1 && '+ 上传'}
                  </Upload>
                </Form.Item>
                <span style={{ color: '#ccc' }}>建议尺寸 45*45 px</span>
              </Form.Item>

              <Form.Item
                label={
                  <span>
                    <span style={{ color: 'red' }}>*</span>名称
                  </span>
                }
              >
                <Form.Item name="name" noStyle rules={[{ required: true, message: '请输入' }]}>
                  <Input placeholder="请输入" />
                </Form.Item>
                <span style={{ color: '#ccc' }}>名称唯一</span>
              </Form.Item>

              <Form.Item label={<span>别名</span>}>
                <Form.Item name="alias" noStyle rules={[{ message: '请输入' }]}>
                  <Input placeholder="请输入" />
                </Form.Item>
                <span style={{ color: '#ccc' }}>名称唯一</span>
              </Form.Item>

              <Form.Item
                label="说明"
                // {...layout}
                name="intro"
                // initialValue={remark}
              >
                <TextArea rows={4} placeholder="请输入" />
              </Form.Item>

              <Form.Item
                label="是否启用"
                // {...layout}
                name="is_show"
                initialValue={1}
              >
                <Radio.Group>
                  <Radio value={1}>是</Radio>
                  <Radio value={0}>否</Radio>
                </Radio.Group>
              </Form.Item>
            </Form>
          </Modal>

    
        </PageContainer>
      </Spin>
    );
  }
}

// 7
// 绑定到本页面，此处就可以拿到请求的值，
// allModels所有models集合，mapping是具体的model的命名空间，mappingData命名空间里面你想取的值
export default connect((allModels) => ({}))(DataConnection);
