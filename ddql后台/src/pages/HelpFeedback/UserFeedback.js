import React from 'react';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Table,
  Select,
  DatePicker,
  Tabs,
  Modal,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
const { TabPane } = Tabs;
const { TextArea } = Input;
import UserFeedbackDetails from './components/UserFeedbackDetails';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
//所有场馆
//所有场馆
//所有场馆
//所有场馆
class Login extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    roleData: [
      { roleId: 1, roleName: '哈哈' },
      { roleId: 2, roleName: '哈哈333' },
    ],
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
          payload: {
            id: this.state.id,
            content: this.state. content,
            phone: this.state.phone,
            status: this.state. status,
            start_time:this.state.start_time,
            end_time:this.state.end_time,
            page: this.state.pageNum,
            limit: 10,
          },
          url: `/api/admin/feedback/lists`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              console.log(res.data.lists);
              this.setState({
                list: res.data.lists,
                total: res.data.count,
              });
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    console.log('selectedRowKeys changed: ', selectedRowKeys);
    this.setState({ selectedRowKeys });
  };

  pageChange = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNum: page,
      },
      () => {
        this.getData();
      },
    );
  };

  showModal = (add, record) => {
    this.setState({
      newVenues: true,
      add,
      edit: record,
    });
  };

  handleOk = () => {
    this.setState({
      newVenues: false,
    });
  };

  showModals = (id) => {
    if (id) {
      this.setState({
        isModalVisible: true,
        idss: id.join(','),
      });
    } else {
      message.error('请选择需要处理的内容');
    }
   
  };

  //删除
  deletes = (ids) => {
    if (ids == undefined) {
      message.error('请选择需要删除的内容');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids.join(','),
        },
        url: `/api/admin/feedback/delete`,
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



  handleOks = () => {
    const { idss } = this.state;
    this.formRef.current.validateFields().then((values) => {
      //编辑
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: idss,
          remark: values.remark,
        },
        // dataName: 'developerListData',
        method: 'POST',
        url: `/api/admin/feedback/process`,
        myData: (res) => {
          if (res.code === 200) {
            message.success(res.message);
            this.setState({
              isModalVisible: false,
            });
            this.getData();
          }
        },
      });
    });
  };

  handleCancels = () => {
    this.setState({
      isModalVisible: false,
    });
  };


  onChange = (value, dateString) => {
    console.log(dateString);
    this.setState({
      start_time: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
      end_time: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
    });
  };

  onFinish = (vas) => {
    console.log(vas);
    this.setState(
      {
        id: vas.id,
        content: vas. content,
        phone: vas.phone,
        status: vas. status,
        start_time:this.state.start_time,
        end_time:this.state.end_time,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        id: undefined,
        content: undefined,
        phone: undefined,
        status: undefined,
        start_time:undefined,
        end_time:undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  render() {
    const {
      gymTypelist = [],
      selectedRowKeys,
      NewRoles,
      total,
      pageNum,
      listss = [],
      isModalVisible,
    } = this.state;
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
        title: '问题和意见',
        dataIndex: 'content',
      },

      {
        title: '联系电话',
        dataIndex: 'phone',
      },

      {
        title: '反馈时间',
        dataIndex: 'created_at',
      },

      {
        title: '处理状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <span>
                {text === 0 ? (
                  <span className="huangse">未处理</span>
                ) : (
                  <span style={{ color: '#ccc' }}>已处理</span>
                )}
              </span>
            </div>
          );
        },
      },
      {
        title: '操作',
        dataIndex: 'gym_types',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record)}>详情</a>

              {record.status == 0 && (
                <a className="mL15" onClick={() => this.showModals([record.id])}>
                  已处理
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
      <PageContainer
        header={{
          title: ``,
        }}
      >
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="ID" name="id">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="问题和意见" name="content">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="联系电话" name="phone">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="处理状态" name="status">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>已处理</Option>
                      <Option value={0}>未处理</Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={5}>
                  <Form.Item label="创建时间" name="created_at">
                    <RangePicker format="YYYY-MM-DD" onChange={this.onChange} />
                  </Form.Item>
                </Col>




                <Col className="gutter-row" span={3} style={{ textAlign: 'right' }}>
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
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>用户反馈</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={() => this.showModals(selectedRowKeys)}>
                    已处理
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
                </div>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              rowSelection={rowSelection}
              rowKey="id"
              columns={columns}
              dataSource={this.state.list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: pageNum,
              }}
              // scroll={{ x: '100%' }}
            />

            {this.state.newVenues && (
              <UserFeedbackDetails
                handleOk={this.handleOk}
                getData={this.getData}
                edit={this.state.edit}
              />
            )}

            <Modal
              title="已处理"
              visible={isModalVisible}
              onOk={this.handleOks}
              onCancel={this.handleCancels}
              destroyOnClose
            >
              <Form ref={this.formRef}>
                <Form.Item label="备注" name="remark">
                  <TextArea rows={4} placeholder="请输入" />
                </Form.Item>
              </Form>
            </Modal>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(Login);
