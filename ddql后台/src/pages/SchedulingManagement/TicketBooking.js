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
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect } from 'umi';
import moment from 'moment';
// import AddAdministrator from './components/AddAdministrator';
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
        this.setState({
          dateString: new Date().Format('yyyy-MM-dd'),
        });

        this.formRef.current.setFieldsValue({
          date: moment(new Date().Format('yyyy-MM-dd'), 'YYYY-MM-DD'),
        });

        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 999,
          },
          url: `/api/admin/stadium/lists`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              this.formRef.current.setFieldsValue({
                stadium_id: res.data.lists.length>0 && res.data.lists[0].id,
              });
              this.setState(
                {
                  stadiumList: res.data.lists,
                  placeId1: res.data.lists.length>0 && res.data.lists[0].id,
                },
                () => {
                  //场馆
                  const { dispatch } = this.props;
                  dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      stadium_id: this.state.placeId1,
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
                        this.setState(
                          {
                            list: res.data.lists,
                            gymId: res.data.lists.length>0 && res.data.lists[0].id,
                            venue: res.data.lists.length>0 && res.data.lists[0].name,
                            venues: res.data.lists.length>0 && res.data.lists[0].name,
                            dateString: new Date().toLocaleDateString().replace(/\//g, '-'),
                          },
                          () => {
                            this.formRef.current.setFieldsValue({
                              gym_id: this.state.gymId,
                              date: moment(
                                new Date().toLocaleDateString().replace(/\//g, '-'),
                                'YYYY-MM-DD',
                              ),
                            });

                            dispatch({
                              type: 'myModel/getSetData',
                              payload: {
                                gym_id: this.state.gymId,
                                date: new Date().toLocaleDateString().replace(/\//g, '-'),
                                limit: 9999,
                              },
                              url: `/api/admin/gym/ticket/schedule/manage`,
                              method: 'GET',
                              myData: (res) => {
                                console.log(res);
                                this.setState({
                                  spinning: false,
                                });
                                if (res && res.code === 200) {
                                  this.setState({
                                    lists: res.data.lists,
                                  });
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
                        // this.setState({ isSelectForm: true });
                      }
                    },
                  });
                },
              );
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

  showModal = (record) => {
    this.setState({
      isModalVisible: true,
      record,
    });
  };

  handleOk = () => {
    this.formRefs.current.validateFields().then((values) => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          gym_id: this.state.record.gym_id,
          site_id: this.state.record.site_id,
          date: this.state.dateString,
          start_time: this.state.record.start_time,
          end_time: this.state.record.end_time,
          reason: values.reason,
        },
        // dataName: 'developerListData',
        method: 'POST',
        url: `/api/admin/gym/site/disable/add`,
        myData: (res) => {
          if (res.code === 200) {
            message.success(res.message);
            this.setState({
              isModalVisible: false,
            });
            this.getData();
          } else {
            message.error(res.message);
          }
        },
      });
    });
  };

  handleCancel = () => {
    this.setState({
      isModalVisible: false,
    });
  };

  // 删除函数
  deletes = (ids) => {
    console.log(ids);
  };

  onFinish = (v) => {
    this.setState({
      gymId: v.gym_id,
      reserve: v.reserve,
    });
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        gym_id: v.gym_id,
        date: v.date && v.date.format('YYYY-MM-DD'),
        reserve: v.reserve,
      },
      url: `/api/admin/gym/ticket/schedule/manage`,
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
          });
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  onChange = (date, dateString) => {
    console.log(dateString);
    this.setState({
      dateString,
    });
  };

  // siteFrontReserve = (v, ids) => {
  //   console.log(ids);

  //   if (ids == undefined) {
  //     message.error('请选择需要操作的场地排期');
  //   } else {
  //     this.props.dispatch({
  //       type: 'myModel/getSetData',
  //       payload: {
  //         front_reserve: v,
  //         id: ids.join(','),
  //       },
  //       url: `/api/admin/gym/ticket/schedule/manage`,
  //       method: 'POST',
  //       myData: (res) => {
  //         if (res && res.code === 200) {
  //           message.success(res.message);
  //           this.getData();
  //         } else {
  //           message.error(res.message);
  //           // this.setState({ isSelectForm: true });
  //         }
  //       },
  //     });
  //   }
  // };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.getData();
  };

  handleChange = (vas, a) => {
    this.setState({
      venues: a.key,
    });
  };

  TheNextDay = (vas) => {
    const { dateString } = this.state;
    this.setState(
      {
        dateString: new Date(
          new Date(`${dateString}`).getFullYear(),
          new Date(`${dateString}`).getMonth(),
          new Date(`${dateString}`).getDate() + 1,
        )
          .toLocaleDateString()
          .replace(/\//g, '-'),
      },
      () => {
        this.formRef.current.setFieldsValue({
          date: moment(this.state.dateString, 'YYYY-MM-DD'),
        });

        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            gym_id: this.state.gymId,
            date: this.state.dateString,
            reserve: this.state.reserve,
          },
          url: `/api/admin/gym/ticket/schedule/manage`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              this.setState({
                lists: res.data.lists,
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

  theDayBefore = (vas) => {
    const { dateString } = this.state;
    this.setState(
      {
        spinning: true,
        dateString: new Date(
          new Date(`${dateString}`).getFullYear(),
          new Date(`${dateString}`).getMonth(),
          new Date(`${dateString}`).getDate() - 1,
        )
          .toLocaleDateString()
          .replace(/\//g, '-'),
      },
      () => {
        this.formRef.current.setFieldsValue({
          date: moment(this.state.dateString, 'YYYY-MM-DD'),
        });

        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            gym_id: this.state.gymId,
            date: this.state.dateString,
            reserve: this.state.reserve,
          },
          url: `/api/admin/gym/ticket/schedule/manage`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              this.setState({
                lists: res.data.lists,
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
    const { lists = [], stadiumList = [], list = [] } = this.state;

    const columns = [
      {
        title: '场馆',
        dataIndex: 'gym_name',
      },
      {
        title: '日期',
        dataIndex: 'updated_at',
        render: (text, record) => {
          return <div>{this.state.dateString}</div>;
        },
      },
      {
        title: '名称',
        dataIndex: 'name',
      },

      {
        title: '时间',
        render: (text, record) => {
          return (
            <div>
              {record.open_start_time}-{record.open_end_time}
            </div>
          );
        },
      },

      {
        title: '价格(元)',
        dataIndex: 'price',
      },

      {
        title: '总量',
        dataIndex: 'total_num',
        render: (text, record) => {
          return <div>{record.total_num == 0 ? '不限' : record.total_num}</div>;
        },
      },

      {
        title: '已销售',
        dataIndex: 'price',
        render: (text, record) => {
          return <div>{record.total_num - record.remain_num}</div>;
        },
      },

      {
        title: '余量',
        dataIndex: 'remain_num',
        render: (text, record) => {
          return <div>{record.total_num == 0 ? '不限' : record.remain_num}</div>;
        },
      },

      {
        title: '预订状态',
        dataIndex: 'reserve_status',
        render: (text, record) => {
          return (
            <div>
              {record.reserve_status == 1 && <span className="huangse">不可预订</span>}
              {record.reserve_status == 2 && <span className="luSe">可预订</span>}
              {record.reserve_status == 3 && <a>已预订</a>}
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
                  <Form.Item label="预订日期" name="date" rules={[{ required: true }]}>
                    <DatePicker onChange={this.onChange} allowClear={false} />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="场所" name="stadium_id" rules={[{ required: true }]}>
                    <Select showSearch optionFilterProp="label" onChange={this.handleChangeS} placeholder="请选择">
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
                  <Form.Item label="场馆" name="gym_id" rules={[{ required: true }]}>
                    <Select showSearch optionFilterProp="label" onChange={this.handleChange} placeholder="请选择">
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
                  <Form.Item label="预订状态" name="reserve">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>不可预订</Option>
                      <Option value={2}>可预订</Option>
                      <Option value={3}>已预订</Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={8} style={{ textAlign: 'right' }}>
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
            {/* <div style={{ textAlign: 'center' }}>
              <a onClick={this.theDayBefore}>前一天</a><span style={{fontSize:20,marginLeft:10,marginRight:10}}> {this.state.dateString}{' '}
              {this.state.venue}</span>  <a onClick={this.TheNextDay}>后一天</a>
            </div> */}

            <Row>
              <Col span={8}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>售票排期</h1>
              </Col>
              <Col span={8}>
                <div style={{ textAlign: 'center' }}>
                  <a onClick={this.theDayBefore}>前一天</a>
                  <span style={{ fontSize: 20, marginLeft: 10, marginRight: 10 }}>
                    {this.state.dateString} {this.state.venue}
                  </span>
                  <a onClick={this.TheNextDay}>后一天</a>
                </div>
              </Col>
              {/* <Col span={8} style={{ textAlign: 'right' }}>
                <Button onClick={() => this.siteFrontReserve(selectedRowKeys)}>禁用</Button>
              </Col> */}
            </Row>

            <Table
              style={{ marginTop: 25 }}
              rowKey="id"
              columns={columns}
              dataSource={lists}
              pagination={false}
            />
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(Login);
