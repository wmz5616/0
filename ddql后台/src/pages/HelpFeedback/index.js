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
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
const { TabPane } = Tabs;
import AddQuestion from './components/AddQuestion';
import TypeOfProblem from './components/TypeOfProblem';

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
    xxx: true,
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
            publish: this.state.publish,
            title: this.state.title,
            qa_types: this.state.qa_types,
            page: this.state.pageNum,
            limit: 10,
          },
          url: `/api/admin/helper/qa/lists`,
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
                total: res.data.total,
              });
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });

        //问题类型
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 999,
          },
          url: `/api/admin/helper/qa/type/lists`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              console.log(res.data.lists);
              this.setState({
                listss: res.data.lists,
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

  // 删除函数
  deletes = (ids) => {
    console.log(ids);
  };

  onFinish = (vas) => {
    console.log(vas);
    this.setState(
      {
        id: vas.id,
        title: vas.title,
        publish: vas.publish,
        qa_types: vas.qa_types,
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
        title: undefined,
        publish: undefined,
        qa_types: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  siteFrontReserve = (v, ids) => {
    console.log(ids);

    if (ids == undefined) {
      message.error('请选择需要操作的问题');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          publish: v,
          id: ids.join(','),
        },
        url: `/api/admin/helper/qa/publish`,
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
      message.error('请选择需要删除的问题');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids.join(','),
        },
        url: `/api/admin/helper/qa/delete`,
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

  callback = (key) => {
    this.setState(
      {
        xxx: false,
        pageNum:1
      },
      () => {
        this.setState({
          xxx: true,
        });
      },
    );
  };

  render() {
    const { gymTypelist = [], selectedRowKeys, NewRoles, total, pageNum, listss = [] } = this.state;
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
        title: '标题',
        dataIndex: 'title',
      },

      {
        title: '类型',
        dataIndex: 'qa_types',
        render: (text, record) => {
          return (
            <div>
              {record.qa_types.length != 0 &&
                record.qa_types.map((res) => {
                  console.log(res.qa_type.title);
                  return (
                    <span value={res.id} key={res.id}>
                      {res.qa_type.title}&nbsp;&nbsp;
                    </span>
                  );
                })}
            </div>
          );
        },
      },
      {
        title: '发布状态',
        dataIndex: 'publish',
        render: (text, record) => {
          return (
            <div>
              <span>
                {text === 1 ? (
                  <span className="luSe">已发布</span>
                ) : (
                  <span className="huangse">未发布</span>
                )}
              </span>
            </div>
          );
        },
      },
      {
        title: '排序',
        dataIndex: 'sort',
      },
      {
        title: '创建时间',
        dataIndex: 'created_at',
      },

      {
        title: '操作',
        dataIndex: 'gym_types',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record)}>编辑</a>
              {record.publish == 1 ? (
                <a className="mL15" onClick={() => this.siteFrontReserve(0, [record.id])}>
                  取消发布
                </a>
              ) : (
                <a className="mL15" onClick={() => this.siteFrontReserve(1, [record.id])}>
                  发布
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
                <a className="mL15" style={{ color: 'red' }}>
                  删除
                </a>
              </Popconfirm>
            </div>
          );
        },
      },
    ];

    return (
      <div className="zxcv">
        <div className="asd">
          <PageContainer
           header={{
            title: ``,
          }}
          >
            <Spin spinning={this.state.spinning}>
              <div style={{ backgroundColor: '#f0f2f5', marginBottom: 15 }}>
                <div style={{ backgroundColor: '#fff' }}>
                  <Tabs defaultActiveKey="1" onChange={this.callback}>
                    <TabPane tab="问题管理" key="1">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          <div
                            style={{
                              backgroundColor: '#f0f2f5',
                              paddingBottom: 15,
                            }}
                          >
                            <div style={{ padding: 24, backgroundColor: '#fff', paddingBottom: 0 }}>
                              <Form ref={this.formRef} onFinish={this.onFinish}>
                                <Row gutter={16}>
                                  <Col className="gutter-row" span={4}>
                                    <Form.Item label="ID" name="id">
                                      <Input placeholder="请输入" />
                                    </Form.Item>
                                  </Col>

                                  <Col className="gutter-row" span={4}>
                                    <Form.Item label="标题" name="title">
                                      <Input placeholder="请输入" />
                                    </Form.Item>
                                  </Col>

                                  <Col className="gutter-row" span={4}>
                                    <Form.Item label="类型" name="qa_types">
                                      <Select
                                        allowClear
                                        showSearch
                                        placeholder="请选择"
                                        optionFilterProp="label"
                                      >
                                        {listss.map((res) => {
                                          return (
                                            <Option
                                              value={res.id}
                                              key={res.id}
                                              label={`${res.id}${res.title}`}
                                            >
                                              {res.title}
                                            </Option>
                                          );
                                        })}
                                      </Select>
                                    </Form.Item>
                                  </Col>

                                  <Col className="gutter-row" span={4}>
                                    <Form.Item label="发布状态" name="publish">
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
                          </div>
                          <div style={{ padding: 24 }}>
                            <Row>
                              <Col span={12}>
                                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>常见问题</h1>
                              </Col>
                              <Col span={12} style={{textAlign:'right'}}>
                                <Button type="primary" onClick={() => this.showModal(true)}>
                                  +新增问题
                                </Button>
                                <Button
                                  className="mL15 bxluSe"
                                  onClick={() => this.siteFrontReserve(1, selectedRowKeys)}
                                >
                                  发布
                                </Button>
                                <Button
                                  className="mL15 bxHuang"
                                  onClick={() => this.siteFrontReserve(0, selectedRowKeys)}
                                >
                                  取消发布
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

                            <Table
                              style={{ marginTop: 25 }}
                              rowSelection={rowSelection}
                              rowKey="id"
                              columns={columns}
                              dataSource={this.state.list}
                              pagination={{
                                showSizeChanger: false,
                                onChange: this.pageChange,
                                pageSize: 10,
                                total:this.state.total,
                                current: pageNum,
                              }}
                              // scroll={{ x: '100%' }}
                            />
                          </div>
                        </div>
                      </div>
                    </TabPane>
                    <TabPane tab="问题类型" key="2">
                    <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                      {this.state.xxx && <TypeOfProblem />}
                      </div>
      </div>
                    </TabPane>
                  </Tabs>
                </div>
              </div>

              {this.state.newVenues && (
                <AddQuestion
                  handleOk={this.handleOk}
                  getData={this.getData}
                  add={this.state.add}
                  edit={this.state.edit}
                />
              )}
            </Spin>
          </PageContainer>
        </div>
      </div>
    );
  }
}

export default connect()(Login);
