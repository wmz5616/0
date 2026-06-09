import React from 'react';
import { UserOutlined, QuestionCircleOutlined } from '@ant-design/icons';
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
import DisableRecording from './components/DisableRecording';
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
    sss: true,
    record: [],
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
        //场所列表

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
            limit: 9999,
          },
          url: `/api/admin/stadium/lists`,
          method: 'GET',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              this.formRef.current.setFieldsValue({
                stadium_id: res.data.lists && res.data.lists[0].id,
              });
              this.setState(
                {
                  stadiumList: res.data.lists,
                  placeId1: res.data.lists && res.data.lists[0].id,
                },
                () => {
                  //获取场馆
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
                        list: res.data.lists,
                        gymId: res.data.lists && res.data.lists[0].id,
                        venue: res.data.lists && res.data.lists[0].name,
                        venues: res.data.lists && res.data.lists[0].name,
                      });
                      if (res && res.code === 200) {
                        if (res.data.lists.length > 0 && res.data.lists[0].id) {
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
                                        dateString: new Date().Format('yyyy-MM-dd'),
                                      },
                                      () => {
                                        console.log(1212);
                                        this.formRef.current.setFieldsValue({
                                          gym_id: this.state.gymId,
                                        });

                                        dispatch({
                                          type: 'myModel/getSetData',
                                          payload: {
                                            gym_id: this.state.gymId,
                                            date: new Date().Format('yyyy-MM-dd'),
                                            limit: 9999,
                                          },
                                          url: `/api/admin/gym/site/schedule/manage`,
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
                                  },
                                );
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

  onSelectChange = (selectedRowKeys, ccc) => {
    //触发表单筛选
    console.log(ccc);
    this.setState({
      selectedRowKeys,
      ccc,
    });
  };

  showModal = (record) => {
    console.log(record);
    this.setState({
      isModalVisible: true,
      record,
    });
  };

  handleOk = () => {
    // this.state.record.map(res=>{

    this.formRefs.current.validateFields().then((values) => {
      this.state.record.map((res) => {
        console.log(res.start_time);
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            gym_id: res.gym_id,
            site_id: res.site_id,
            date: this.state.dateString,
            start_time: res.start_time && res.start_time,
            end_time: res.end_time && res.end_time,
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
              this.onFinishs();
            } else {
              message.error(res.message);
            }
          },
        });
      });
    });

    // })
  };

  onFinishs = (v) => {
    console.log(this.state.dateString);
    this.setState({
      gymId: this.state.gymId,
      reserve: this.state.reserve,
      disable: this.state.disable,
      spinning: true,
      site_id: this.state.site_id,
    });
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        gym_id: this.state.gymId,
        date: this.state.dateString,
        reserve: this.state.reserve,
        disable: this.state.disable,
        site_id: this.state.site_id,
      },
      url: `/api/admin/gym/site/schedule/manage`,
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
      disable: v.disable,
      spinning: true,
      site_id: v.site_id,
    });
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        gym_id: v.gym_id,
        date: v.date && v.date.format('YYYY-MM-DD'),
        reserve: v.reserve,
        disable: v.disable,
        site_id: v.site_id,
      },
      url: `/api/admin/gym/site/schedule/manage`,
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
        url: `/api/admin/gym/front_reserve`,
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
    this.getData();
  };

  handleChange = (value, a) => {
    this.setState({
      venues: a.key,
    });
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

  TheNextDay = (vas) => {
    const { dateString } = this.state;
    this.setState(
      {
        spinning: true,
        dateString: new Date(
          new Date(`${dateString}`).getFullYear(),
          new Date(`${dateString}`).getMonth(),
          new Date(`${dateString}`).getDate() + 1,
        ).Format('yyyy-MM-dd'),
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
            disable: this.state.disable,
            site_id: this.state.site_id,
          },
          url: `/api/admin/gym/site/schedule/manage`,
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
        ).Format('yyyy-MM-dd'),
      },
      () => {
        //当天日期
        const xs =
          new Date().getFullYear() +
          '-' +
          (new Date().getMonth() + 1 == 12 ? '01' : new Date().getMonth() + 1) +
          '-' +
          new Date().getDate();
        const aa = new Date(xs).getTime() / 1000;

        console.log(xs);

        // console.log(this.state.)
        // if ((new Date(this.state.dateString).getTime()/1000) >=aa) {
        this.formRef.current.setFieldsValue({
          date: moment(this.state.dateString, 'YYYY-MM-DD'),
        });

        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            gym_id: this.state.gymId,
            date: this.state.dateString,
            reserve: this.state.reserve,
            disable: this.state.disable,
            site_id: this.state.site_id,
          },
          url: `/api/admin/gym/site/schedule/manage`,
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
        // }
        //  else {
        //   message.error('不可选择过去日期')
        //   this.setState({
        //     spinning: false,
        //     dateString: new Date(
        //       new Date(`${dateString}`).getFullYear(),
        //       new Date(`${dateString}`).getMonth(),
        //       new Date(`${dateString}`).getDate() ,
        //     )
        //       .toLocaleDateString()
        //       .replace(/\//g, '-'),
        //   });
        // }
      },
    );
  };

  callback = (xx) => {
    // if (xx == 1) {
    //   this.getData();
    // }

    this.setState(
      {
        sss: false,
      },
      () => {
        this.setState({
          sss: true,
        });
      },
    );
  };

  siteFrontReserve = (ids) => {
    console.log(ids);

    if (ids == undefined) {
      message.error('请选择需要操作的场馆');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids.join(','),
        },
        url: `/api/admin/gym/front_reserve`,
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

  disabledDate = (current) => {
    // Can not select days before today and today
    return current && current < moment().endOf('day');
  };

  // disabledDateTime=()=> {
  //     return {
  //       disabledHours: () => range(0, 24).splice(4, 20),
  //       disabledMinutes: () => range(30, 60),
  //       disabledSeconds: () => [55, 56],
  //     };
  //   }

  showConfirm = (record) => {
    Modal.info({
      title: '锁场原因',
      content: (
        <div>
          <p style={{ fontSize: 14 }}>{record.disable_reason}</p>
          <p style={{ fontSize: 12 }}>
            {record.disable_username}（{record.disable_phone}）- {record.disable_time}
          </p>
        </div>
      ),
      onOk() {},
    });
  };

  render() {
    const {
      lists = [],
      isModalVisible,
      list = [],
      selectedRowKeys,
      listxx = [],
      stadiumList = [],
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };
    const columns = [
      {
        title: '排期ID',
        dataIndex: 'sch_id',
      },
      {
        title: '场馆',
        dataIndex: 'gym_name',
      },
      {
        title: '场地',
        dataIndex: 'site_name',
      },
      {
        title: '日期',
        dataIndex: 'date',
        render: (text, record) => {
          return <div>{this.state.dateString}</div>;
        },
      },
      {
        title: '时间',
        dataIndex: 'extractData',
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
        dataIndex: 'duration',
      },

      {
        title: '价格(元)',
        dataIndex: 'price',
        render: (text, record) => {
          // var time =record.start_time
          // var hour = time.split(':')[0];
          // var min = time.split(':')[1];
          // var startTimes = Number(hour * 3600) + Number(min * 60);

          // var times =record.end_time
          // var hours = times.split(':')[0];
          // var mins = times.split(':')[1];
          // var endSeconds = Number(hours * 3600) + Number(mins * 60);
          return (
            <div>
              {/* {((endSeconds  - startTimes) / 1800 ) * record.price} */}
              {((record.end_seconds - record.start_seconds) / 60 / record.gym.site_bill_unit) *
                record.price}
            </div>
          );
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

      {
        title: '后台锁场',
        dataIndex: 'disable_status',
        render: (text, record) => {
          return (
            <div>
              <span>
                {text === 2 ? (
                  <span className="luSe">未锁场</span>
                ) : (
                  <span className="huangse">
                    已锁场
                    <a onClick={() => this.showConfirm(record)}>
                      <QuestionCircleOutlined style={{ marginLeft: 10 }} />
                    </a>
                  </span>
                )}
              </span>
            </div>
          );
        },
      },

      {
        title: '操作',
        dataIndex: 'extractData',
        render: (text, record) => {
          return (
            <div>
              {record.disable_status == 2 && <a onClick={() => this.showModal([record])}>锁场</a>}
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
                    <TabPane tab="场地排期" key="1">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          <>
                            <div
                              style={{
                                backgroundColor: '#f0f2f5',
                                paddingBottom: 15,
                              }}
                            >
                              <div
                                style={{ padding: 24, backgroundColor: '#fff', paddingBottom: 0 }}
                              >
                                <Form ref={this.formRef} onFinish={this.onFinish}>
                                  <Row gutter={16}>
                                    <Col className="gutter-row" span={4}>
                                      <Form.Item
                                        label="预订日期"
                                        name="date"
                                        rules={[{ required: true }]}
                                      >
                                        <DatePicker
                                          onChange={this.onChange}
                                          allowClear={false}
                                          // disabledDate={this.disabledDate}
                                          style={{ width: '100%' }}
                                        />
                                      </Form.Item>
                                    </Col>

                                    <Col className="gutter-row" span={4}>
                                      <Form.Item
                                        label="场所"
                                        name="stadium_id"
                                        rules={[{ required: true }]}
                                      >
                                        <Select
                                          showSearch
                                          optionFilterProp="label"
                                          onChange={this.handleChangeS}
                                          placeholder="请选择"
                                        >
                                          {stadiumList.map((res) => {
                                            return (
                                              <Option
                                                value={res.id}
                                                key={res.name}
                                                label={`${res.id}${res.name}`}
                                              >
                                                {res.name}
                                              </Option>
                                            );
                                          })}
                                        </Select>
                                      </Form.Item>
                                    </Col>

                                    <Col className="gutter-row" span={4}>
                                      <Form.Item
                                        label="场馆"
                                        name="gym_id"
                                        rules={[{ required: true }]}
                                      >
                                        <Select
                                          showSearch
                                          optionFilterProp="label"
                                          onChange={this.handleChange}
                                          placeholder="请选择"
                                        >
                                          {list.map((res) => {
                                            return (
                                              <Option
                                                value={res.id}
                                                key={res.name}
                                                label={`${res.id}${res.name}`}
                                              >
                                                {res.name}
                                              </Option>
                                            );
                                          })}
                                        </Select>
                                      </Form.Item>
                                    </Col>

                                    <Col className="gutter-row" span={3}>
                                      <Form.Item label="场地" name="site_id">
                                        <Select
                                          allowClear
                                          showSearch
                                          optionFilterProp="label"
                                          placeholder="请选择"
                                        >
                                          {listxx.map((res) => {
                                            return (
                                              <Option
                                                value={res.id}
                                                key={res.name}
                                                label={`${res.id}${res.name}`}
                                              >
                                                {res.name}
                                              </Option>
                                            );
                                          })}
                                        </Select>
                                      </Form.Item>
                                    </Col>
                                    <Col className="gutter-row" span={3}>
                                      <Form.Item label="预订状态" name="reserve">
                                        <Select allowClear placeholder="请选择">
                                          <Option value={1}>不可预订</Option>
                                          <Option value={2}>可预订</Option>
                                          <Option value={3}>已预订</Option>
                                        </Select>
                                      </Form.Item>
                                    </Col>

                                    <Col className="gutter-row" span={3}>
                                      <Form.Item label="后锁场" name="disable">
                                        <Select allowClear placeholder="请选择">
                                          <Option value={1}>已锁场</Option>
                                          <Option value={2}>未锁场</Option>
                                        </Select>
                                      </Form.Item>
                                    </Col>

                                    <Col
                                      className="gutter-row"
                                      span={3}
                                      style={{ textAlign: 'right' }}
                                    >
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
                                <Col span={8}>
                                  <h1 style={{ fontWeight: '600', fontSize: '18px' }}>场地排期</h1>
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
                                <Col span={8} style={{ textAlign: 'right' }}>
                                  <Button onClick={() => this.showModal(this.state.ccc)}>
                                    锁场
                                  </Button>
                                </Col>
                              </Row>

                              <Table
                                style={{ marginTop: 25 }}
                                rowKey="sch_id"
                                columns={columns}
                                dataSource={lists}
                                pagination={false}
                                rowSelection={rowSelection}
                              />
                            </div>
                          </>
                        </div>
                      </div>
                    </TabPane>
                    <TabPane tab="锁场记录" key="2">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          {/* {this.state.sss && <DisableRecording />} */}
                          <DisableRecording />
                        </div>
                      </div>
                    </TabPane>
                  </Tabs>
                </div>
              </div>
              <Modal
                title="锁场"
                visible={isModalVisible}
                onOk={this.handleOk}
                onCancel={this.handleCancel}
                destroyOnClose
              >
                <Form ref={this.formRefs}>
                  <Form.Item label="锁场原因" name="reason" rules={[{ required: true }]}>
                    <TextArea rows={4} placeholder="请输入" />
                  </Form.Item>
                </Form>
              </Modal>
            </Spin>
          </PageContainer>
        </div>
      </div>
    );
  }
}

export default connect()(Login);
