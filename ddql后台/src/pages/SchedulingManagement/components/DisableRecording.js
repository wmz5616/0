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
  Modal,
  Table,
  Select,
  DatePicker,
  Tabs,
  Popconfirm,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect } from 'umi';
import moment from 'moment';
import AddDisableRecording from './AddDisableRecording';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { TextArea } = Input;
const { RangePicker } = DatePicker;
const { TabPane } = Tabs;
//场地排期
//场地排期
//场地排期
//场地排期
class Login extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
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

        
        Date.prototype.Format = function (fmt) {
          var o = {
            'M+': this.getMonth() + 1, //月份
            'd+': this.getDate(), //日
            'h+': this.getHours(), //小时
            'm+': this.getMinutes(), //分
            's+': this.getSeconds(), //秒
            'q+': Math.floor((this.getMonth() + 3) / 3), //季度
            S: this.getMilliseconds(), //毫秒
          };
          if (/(y+)/.test(fmt))
            fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
          for (var k in o)
            if (new RegExp('(' + k + ')').test(fmt))
              fmt = fmt.replace(
                RegExp.$1,
                RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length),
              );
          return fmt;
        };

        // this.setState({
        //   dateString: new Date().Format('yyyy-MM-dd'),
        //   start_time: new Date().Format('yyyy-MM-dd'),
        //   end_time: new Date().Format('yyyy-MM-dd'),
        // })

        // this.formRef.current.setFieldsValue({
        //   date: [
        //     moment(new Date().Format('yyyy-MM-dd'), 'YYYY-MM-DD'),
        //     moment(new Date().Format('yyyy-MM-dd'), 'YYYY-MM-DD'),
        //   ],
        // });


        //场所列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 9999,
          },
          url: `/api/admin/stadium/lists`,
          method: 'GET',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              // this.formRef.current.setFieldsValue({
              //   stadium_id: res.data.lists && res.data.lists[res.data.lists.length-1].id,
              // });
              this.setState(
                {
                  stadiumList: res.data.lists,
                  // placeId1: res.data.lists && res.data.lists[res.data.lists.length-1].id,
                },
                () => {
                  //场馆
                  const { dispatch } = this.props;
                  dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      // stadium_id: this.state.placeId1,
                      limit: 999,
                    },
                    url: `/api/admin/gym/lists`,
                    method: 'GET',
                    myData: (res) => {
                      if (res && res.code === 200) {
                        this.setState({
                          list: res.data.lists,
                        });
                        if (res.data.lists.length>0 && res.data.lists[0].id) {
                          //场地
                          dispatch({
                            type: 'myModel/getSetData',
                            payload: {
                              gym_id: res.data.lists[0].id,
                              limit: 9999,
                            },
                            url: `/api/admin/gym/site/lists`,
                            method: 'GET',
                            myData: (res) => {
                              if (res && res.code === 200) {
                              
                                this.setState(
                                  {
                                    listxx: res.data.lists,
                                  },
                                  () => {
                                    this.setState(
                                      {
                                        // gym_id: this.state.list.length > 0 && this.state.list[0].id,
                                        // site_id: res.data.lists && res.data.lists[0].id,
                                        venue: res.data.lists.length > 0 && res.data.lists[0].name,
                                        // dateString: new Date().Format('yyyy-MM-dd'),
                                        // start_time: new Date().Format('yyyy-MM-dd'),
                                        // end_time: new Date().Format('yyyy-MM-dd'),
                                      },
                                      () => {
                                        // this.formRef.current.setFieldsValue({
                                        //   // site_id: this.state.site_id,
                                        //   gym_id: this.state.gym_id,
                                        //   date: [
                                        //     moment(new Date().Format('yyyy-MM-dd'), 'YYYY-MM-DD'),
                                        //     moment(new Date().Format('yyyy-MM-dd'), 'YYYY-MM-DD'),
                                        //   ],
                                        // });

                                        dispatch({
                                          type: 'myModel/getSetData',
                                          payload: {
                                            // gym_id: this.state.gym_id,
                                            // start_time: new Date().Format('yyyy-MM-dd'),
                                            // end_time: new Date().Format('yyyy-MM-dd'),
                                            limit: 10,
                                            page: this.state.pageNum,
                                          },
                                          url: `/api/admin/gym/site/disable/lists`,
                                          method: 'GET',
                                          myData: (res) => {
                                            console.log(res);
                                            this.setState({
                                              spinning: false,
                                            });
                                            if (res && res.code === 200) {
                                              this.setState({
                                                lists: res.data.lists,
                                                total: res.data.total,
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
                              } else {
                                
                                message.error(res.msg);
                              }
                            },
                          });
                        }
                      } else {
                        message.error(res.message);
                        // this.setState({ isSelectForm: true });
                      }
                    },
                  });
                },
              );
            } else {
              message.error(res.message);
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

  showModal = (add, record) => {
    this.setState({
      isModalVisible: true,
      record,
      add,
    });
  };

  handleCancel = () => {
    this.setState({
      isModalVisible: false,
    });
  };

  //删除
  deletes = (ids) => {
    if (ids == undefined) {
      message.error('请选择需要删除的记录');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids.join(','),
        },
        url: `/api/admin/gym/site/disable/delete`,
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

  onFinish = (v) => {
    this.setState({
      gym_id: v.gym_id,
      reserve: v.reserve,
      site_id: v.site_id,
      reason: v.reason,
      start_time: this.state.start_time,
      end_time: this.state.end_time,
      stadium_id:v.stadium_id,
      pageNum: 1,
    });
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        stadium_id:v.stadium_id,
        gym_id: v.gym_id,
        site_id: v.site_id,
        start_time: this.state.start_time,
        end_time: this.state.end_time,
        reason: v.reason,
        page: this.state.pageNum,
        limit: 10,
      },
      url: `/api/admin/gym/site/disable/lists`,
      method: 'GET',
      myData: (res) => {
        console.log(res);
        this.setState({
          spinning: false,
          venue: this.state.venues,
        });
        if (res && res.code === 200) {
          this.setState({
            lists: res.data.lists,
            total: res.data.total,
          });
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  onChange = (value, dateString) => {
    console.log(dateString);
    this.setState({
      start_time: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
      end_time: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
    });
  };

  siteFrontReserve = (v, ids) => {
    console.log(ids);

    if (ids == undefined) {
      message.error('请选择需要操作的场地排期');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          front_reserve: v,
          id: ids.join(','),
        },
        url: `/api/admin/gym/ticket/schedule/manage`,
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

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState({
      site_id: undefined,
      reason: undefined,
      gym_id:undefined,
      pageNum:1
    });
    this.getData();
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    console.log('selectedRowKeys changed: ', selectedRowKeys);
    this.setState({ selectedRowKeys });
  };

  handleChange = (value) => {
    // this.setState({
    //   gym_id:undefined,
    //   pageNum:1
    // })
    if (value) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          gym_id: value,
        },
        url: `/api/admin/gym/site/lists`,
        method: 'GET',
        myData: (res) => {
          console.log(res);
          this.setState({
            spinning: false,
          });
          if (res && res.code === 200) {
            this.setState({
              listxx: res.data.lists,
              limit: 999,
            });
            this.formRef.current.setFieldsValue({
              site_id: undefined,
            });
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  pageChange = (page) => {
    console.log(page);
    //列表改变页码
    this.setState(
      {
        pageNum: page,
      },
      () => {
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            stadium_id:this.state.stadium_id,
            gym_id:this.state.gym_id,
            site_id: this.state.site_id,
            start_time: this.state.start_time,
            end_time: this.state.end_time,
            reason: this.state.reason,
            page: this.state.pageNum,
            limit: 10,
          },
          url: `/api/admin/gym/site/disable/lists`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              this.setState({
                lists: res.data.lists,
                total: res.data.total,
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

  handleChangeS = (value, a) => {
    this.setState({
      venues: a.key,
    });
    if (value) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          stadium_id: value,
          limit: 999,
        },
        url: `/api/admin/gym/lists`,
        method: 'GET',
        myData: (res) => {
          console.log(res);
          this.setState({
            spinning: false,
          });
          if (res && res.code === 200) {
            this.setState({
              list: res.data.lists,
              limit: 999,
            });
            this.formRef.current.setFieldsValue({
              gym_id: undefined,
            });
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  render() {
    const { lists = [], stadiumList=[], list = [], selectedRowKeys, listxx = [] } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const columns = [
      {
        title: '场所',
        dataIndex: 'gym',
        render: (text, record) => {
          return <div>{record.stadium && record.stadium.name}</div>;
        },
      },
      {
        title: '场馆',
        dataIndex: 'gym',
        render: (text, record) => {
          return <div>{record.gym && record.gym.name}</div>;
        },
      },

      {
        title: '场地',
        dataIndex: 'site',
        render: (text, record) => {
          return <div>{record.site && record.site.name}</div>;
        },
      },

      {
        title: '日期',
        dataIndex: 'date',
        render: (text, record) => {
          return <div>{record.date.substr(0, record.date.indexOf('00'))}</div>;
        },
      },

      {
        title: '时间',
        render: (text, record) => {
          return (
            <div>
              {record.start_time}-{record.end_time}
            </div>
          );
        },
      },
      {
        title: '时长(分钟)',
        dataIndex: 'price',
        render: (text, record) => {
          return <div>{(record.end_seconds - record.start_seconds) / 60}</div>;
        },
      },
      {
        title: '禁用原因',
        dataIndex: 'reason',
      },

      {
        title: '创建时间',
        dataIndex: 'created_at',
      },

      {
        title: '操作',
        dataIndex: 'reserve_status',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false, record)}>编辑</a>{' '}
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
        <Spin spinning={this.state.spinning}>
          <div>
            <div style={{ backgroundColor: '#f0f2f5', paddingBottom: 15 }}>
              <div style={{ padding: 24, backgroundColor: '#fff', paddingBottom: 0 }}>
                <Form ref={this.formRef} onFinish={this.onFinish}>
                  <Row gutter={16}>
                    <Col className="gutter-row" span={5}>
                      <Form.Item label="预订日期" name="date">
                        <RangePicker format="YYYY-MM-DD" onChange={this.onChange} />
                      </Form.Item>
                    </Col>

                    <Col className="gutter-row" span={4}>
                      <Form.Item label="场所" name="stadium_id" rules={[{ required: true }]}>
                        <Select showSearch optionFilterProp="label" onChange={this.handleChangeS}  placeholder="请选择">
                          {stadiumList.map((res) => {
                            return (
                              <Option value={res.id} key={res.name} label={`${res.id}${res.name}`}>
                                {res.name}
                              </Option>
                            );
                          })}
                        </Select>
                      </Form.Item>
                    </Col>

                    <Col className="gutter-row" span={4}>
                      <Form.Item label="场馆" name="gym_id">
                        <Select
                          allowClear
                          showSearch
                          optionFilterProp="label"
                          onChange={this.handleChange}
                          placeholder="请选择"
                        >
                          {list.map((res) => {
                            return (
                              <Option value={res.id} key={res.name} label={`${res.id}${res.name}`}>
                                {res.name}
                              </Option>
                            );
                          })}
                        </Select>
                      </Form.Item>
                    </Col>

                    <Col className="gutter-row" span={4}>
                      <Form.Item label="场地" name="site_id">
                        <Select allowClear showSearch optionFilterProp="label" placeholder="请选择">
                          {listxx.map((res) => {
                            return (
                              <Option value={res.id} key={res.name} label={`${res.id}${res.name}`}>
                                {res.name}
                              </Option>
                            );
                          })}
                        </Select>
                      </Form.Item>
                    </Col>

                    <Col className="gutter-row" span={4}>
                      <Form.Item label="禁用原因" name="reason">
                        <Input placeholder="请输入" />
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
            </div>
            <div style={{ textAlign: 'right', padding: 24 }}>
              <span style={{ fontSize: 18, float: 'left' }}>
                <b>场地排期</b>
              </span>
              <Button type="primary" onClick={() => this.showModal(true)}>
                +新增禁用记录
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
              <Table
                style={{ marginTop: 15 }}
                rowKey="id"
                columns={columns}
                dataSource={lists}
                rowSelection={rowSelection}
                pagination={{
                  showSizeChanger: false,
                  onChange: this.pageChange,
                  pageSize: 10,
                  total: this.state.total,
                  current: this.state.pageNum,
                }}
              />
            </div>
          </div>

          {this.state.isModalVisible && (
            <AddDisableRecording
              handleCancel={this.handleCancel}
              getData={this.pageChange}
              record={this.state.record}
              add={this.state.add}
            />
          )}
        </Spin>
      </>
    );
  }
}

export default connect()(Login);
