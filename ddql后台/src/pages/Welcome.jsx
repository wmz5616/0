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
  Divider,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { handleExport } from '../utils/utils';
import { history, connect, Link } from 'umi';
const { TabPane } = Tabs;
import ReactDOM from 'react-dom';
import { DualAxes } from '@ant-design/plots';
import { each, groupBy } from '@antv/util';
const { Option } = Select;
const { RangePicker } = DatePicker;
import moment from 'moment';
import dayjs from 'dayjs';
class Loginx extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    spinnings: false,
    xxxx: 3,
    xxxxx: 3,
    sss: [],
    qqq: [],
    timeType: 'week',
    todayData: { checkInStat: {}, coinStat: {}, exchangeOrderStat: {} },
    timeTypes: 'week',
    sevenDays: [],
  };

  componentDidMount() {
    // this.setState(
    //   {
    //     orderStartDate: moment().startOf('month').format('YYYY-MM-DD 00:00:00'),
    //     orderEndDate: moment().endOf('month').format('YYYY-MM-DD 23:59:59'),
    //   },
    //   () => {
    //     this.getData();
    //     this.getDatax();
    //     // this.getFlow();
    //     this.getIncome();
    //     console.log(this.state.orderStartDate, this.state.orderEndDate);
    //     this.formRef.current.setFieldsValue({
    //       orderDate: [dayjs(moment().startOf('month')), dayjs(moment().endOf('month'))],
    //     });
    //   },
    // );
    this.getData();
    this.getIncome();
  }

  getData = () => {
    const params = {
      timeDimension: this.state.timeTypes,
    };
    if (this.state.timeTypes == 'custom') {
      params.startTime = this.state.start_times;
      params.endTime = this.state.end_times;
    }
    const { dispatch } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: { ...params },
      url: `/ddql/monitor/system/stat`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            todayData: res.data, //今天
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  getIncome = () => {
    const params = {
      timeDimension: this.state.timeTypes,
    };
    if (this.state.timeTypes == 'custom') {
      params.startTime = this.state.start_times;
      params.endTime = this.state.end_times;
    }
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: `/ddql/monitor/place/check_in/rank/lists`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            spinning: false,
            sevenDays: res.data,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  getFlow = () => {
    //平台流量情况
    const params = {
      timeDimension: this.state.timeType,
    };
    console.log(this.state.start_time, this.state.end_time);
    if (this.state.timeType == 'custom') {
      params.startTime = this.state.start_time;
      params.endTime = this.state.end_time;
    }
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: `/ddql/general/statistics`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
          // message.success(res.msg);
          const statSs = [];
          for (let i in res.data) {
            let obj = {
              date: res.data[i].time,
              浏览量: res.data[i].visit_pv,
              访客数: res.data[i].visit_uv,
            };
            statSs.push(obj);
          }
          this.setState({
            statS: statSs,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  // 门店订单完成情况统计
  getDatax = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        startTime: this.state.orderStartDate,
        endTime: this.state.orderEndDate,
      },
      url: `/ddql/general/order/shop/count`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
          // message.success(res.message);
          this.setState({
            VenueRanking: res.data,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  onChange = (value, dateString) => {
    this.setState(
      {
        orderStartDate: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
        orderEndDate: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
        spinning: true,
      },
      () => {
        this.getDatax();
      },
    );
  };

  onChangevv = (value, dateString) => {
    console.log(dateString);
    this.setState(
      {
        start_time: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
        end_time: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
      },
      () => {
        if (this.state.start_time != undefined) {
          // this.getFlow();
        }
      },
    );
  };

  flowSearch = (v) => {
    this.setState(
      {
        timeType: v,
      },
      () => {
        if (this.state.timeType != 'custom') {
          // this.getFlow();
        }
      },
    );
  };

  flowSearchs = (v) => {
    this.setState(
      {
        timeTypes: v,
      },
      () => {
        if (this.state.timeTypes != 'custom') {
          this.getIncome();
          this.getData()
        }
      },
    );
  };

  onChangevvv = (value, dateString) => {
    console.log(dateString);
    this.setState(
      {
        start_times: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
        end_times: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
      },
      () => {
        if (this.state.start_times != undefined) {
          this.getData()
          this.getIncome();
        }
      },
    );
  };

  render() {
    const {
      sevenDays = [],
      today = {},
      totalss = {},
      VenueRanking = [],
      VenueRankings = [],
      sss,
      qqq,
      statS = [],
      userStat = {},
      activeStat = {},
    } = this.state;

    //本周
    // const date =new Date();

    // date.setDate(date.getDate()-date.getDay()+1)
    // const begin=date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()
    // console.log(sss);

    const routes = [
      {
        path: `/`,
        breadcrumbName: '',
      },
    ];

    const annotations = [];
    each(groupBy(sss, 'time'), (values, k) => {
      const value = values.reduce((a, b) => a + b.value, 0);
      // console.log(value);
      annotations.push({
        type: 'text',
        position: [k, value],
        content: `${value}`,
        // style: {
        //   textAlign: 'center',
        //   fontSize: 14,
        //   fill: 'rgba(0,0,0,0.85)',
        // },
        offsetY: -10,
      });
    });
    // console.log([sss, qqq])
    // console.log(annotations);

    const config = {
      tooltip: {
        customContent: (title, data) => {
          console.log(data);
          return (
            <div
              style={{
                padding: '5px 8px',
                lineHeight: '25px',
                width: 180,
                zIndex: 100,
                position: 'relative',
              }}
            >
              <div>{title}</div>
              <div style={{ width: '100%' }}>
                <span style={{ color: '#6395f9', marginRight: 10, fontSize: 20, width: 20 }}>
                  ●
                </span>
                <span>{data?.length > 0 && data[0]?.name}：</span>
                <span style={{ float: 'right', marginTop: 5 }}>
                  {data?.length > 0 && Number(data[0]?.value).toFixed(2)}
                </span>
              </div>
              <div style={{ width: '100%', clear: 'both' }}>
                <span style={{ color: '#98f1ec', marginRight: 10, fontSize: 20, width: 20 }}>
                  ●
                </span>
                <span>{data?.length > 0 && data[1].name}：</span>
                <span style={{ float: 'right', marginTop: 5 }}>
                  {data?.length > 0 && Number(data[1].value).toFixed(2)}
                </span>
              </div>
            </div>
          );
        },
      },
      data: [sss, qqq],
      isStack: true,
      xField: 'time',
      yField: ['订单数量', '订单收入'],
      animation: false,
      geometryOptions: [
        {
          geometry: 'column',
          isStack: true,
          minColumnWidth: 5,
          maxColumnWidth: 60,
          color: ['#6395f9'],
          layout: 'horizontal',
          marker: {
            Symbol: 'circle',
          },
          legend: {
            layout: 'horizontal',
            marker: {
              Symbol: 'circle',
            },
          },
        },
        {
          geometry: 'line',
          color: '#84d6ad',
        },
      ],

      yAxis: {
        // 格式化左坐标轴
        value: {
          grid: {
            line: {
              style: {
                // stroke: '#d5d5d5',
                lineWidth: 1,
                lineDash: [7, 4],
                strokeOpacity: 0.5,
                shadowColor: '#d5d5d5',
              },
            },
          },
        },
      },

      legend: {
        layout: 'horizontal',
        marker: {
          Symbol: 'square',
        },
      },
      // annotations
    };

    const configs = {
      tooltip: {
        customContent: (title, data) => {
          console.log(data);
          return (
            <div
              style={{
                padding: '5px 8px',
                lineHeight: '25px',
                width: 180,
                zIndex: 100,
                position: 'relative',
              }}
            >
              <div>{title}</div>
              <div style={{ width: '100%' }}>
                <span style={{ color: '#98f1ec', marginRight: 10, fontSize: 20, width: 20 }}>
                  ●
                </span>
                <span>{data?.length > 0 && data[0]?.name}：</span>
                <span style={{ float: 'right', marginTop: 5 }}>
                  {data?.length > 0 && Number(data[0]?.value).toFixed(2)}
                </span>
              </div>
              <div style={{ width: '100%', clear: 'both' }}>
                <span style={{ color: '#6395f9', marginRight: 10, fontSize: 20, width: 20 }}>
                  ●
                </span>
                <span>{data?.length > 0 && data[1].name}：</span>
                <span style={{ float: 'right', marginTop: 5 }}>
                  {data?.length > 0 && Number(data[1].value).toFixed(2)}
                </span>
              </div>
            </div>
          );
        },
      },
      data: [statS, statS],
      xField: 'time',
      yField: ['浏览量', '访客数'],
      animation: false,
      height: 300,
      geometryOptions: [
        {
          geometry: 'column',
          minColumnWidth: 5,
          maxColumnWidth: 45,
        },
        {
          geometry: 'line',
          seriesField: '',
          lineStyle: {
            lineWidth: 2,
            // stroke: 'red',
          },
        },
      ],

      yAxis: {
        // 格式化左坐标轴
        浏览量: {
          grid: {
            line: {
              style: {
                // stroke: '#d5d5d5',
                lineWidth: 1,
                lineDash: [7, 4],
                strokeOpacity: 0.5,
                shadowColor: '#d5d5d5',
              },
            },
          },
        },
      },

      legend: {
        layout: 'horizontal',
        marker: {
          Symbol: 'square',
        },
        position: 'top-left',
        offsetX: 150,
        offsetY: Number(-3),
      },

      // yAxis: {
      //   line: { style: { stroke: '#0A122E' }},// 配上这条数据才会显示y轴 stroke等同css color
      //   // label 配置y轴文字的样式
      //  label: {
      //    // formatter 对y轴文字进一步处理
      //    formatter: (v) => `${v}`.replace(/\d{1,3}(?=(\d{3})+$)/g, (s) => `${s},`),
      //    style: {
      //      stroke: '#0A122E',
      //      fontSize: 12,
      //      fontWeight: 300,
      //      fontFamily: 'Apercu',
      //    },
      //  },
      //   grid: {
      //     line: {
      //       style: {
      //         stroke: 'red',
      //         stroke: 2,
      //         lineDash: [4, 5],
      //         strokeOpacity: 0.7,
      //         shadowColor: 'black',
      //         shadowBlur: 10,
      //         shadowOffsetX: 5,
      //         shadowOffsetY: 50,
      //         cursor: 'pointer'
      //       }
      //     },
      //   },
      // },
    };

    const columns = [
      {
        title: '排名',
        dataIndex: 'rank',
      },
      {
        title: '设备名称',
        dataIndex: 'equipmentName',
      },
      {
        title: '场地名称',
        dataIndex: 'placeName',
      },
      {
        title: '打卡类型',
        dataIndex: 'checkInTypeName',
      },
      {
        title: '打卡量',
        dataIndex: 'checkInNum',
      },
    ];

    return (
      <div className="qqq">
        <div className="www">
          <PageContainer
            header={{
              title: ``,
              breadcrumb: {
                itemRender: this.itemRender,
                routes,
              },
            }}
          >
            <div style={{ backgroundColor: '#f0f2f5' }}>
              <div style={{ backgroundColor: '#fff', padding: 24 }}>
                <Row>
                  <Col span={12}>
                    <h3 style={{ margin: 0 }}>数据总览</h3>
                  </Col>
                  <Col span={12}>
                    <div style={{ position: 'absolute', right: 0 }}>
                      <span
                        className={this.state.timeTypes == 'week' ? 'uu' : 'uuu'}
                        onClick={() => this.flowSearchs('week')}
                      >
                        本周
                      </span>
                      <span
                        className={this.state.timeTypes == 'month' ? 'uu' : 'uuu'}
                        onClick={() => this.flowSearchs('month')}
                      >
                        本月
                      </span>
                      <span
                        className={this.state.timeTypes == 'year' ? 'uu' : 'uuu'}
                        onClick={() => this.flowSearchs('year')}
                      >
                        全年
                      </span>
                      <span className="uuu" onClick={() => this.flowSearchs('custom')}>
                        <RangePicker format="YYYY-MM-DD" onChange={this.onChangevvv} />
                      </span>
                    </div>
                  </Col>
                </Row>
                <Row gutter={24} style={{ marginBottom: 20, marginTop: 30 }}>
                  <Col className="gutter-row" style={{ width: '25%' }}>
                    <div style={{ border: '1px solid #eee', padding: 20, backgroundColor: '#fff' }}>
                      <span style={{ color: 'rgba(0, 0, 0, 0.427)', fontSize: 15 }}>
                        打卡次数（开始打卡+打卡离场)
                      </span>
                      <h1>{this.state.todayData.checkInStat.totalNum}次</h1>
                    </div>
                  </Col>
                  <Col className="gutter-row" style={{ width: '25%' }}>
                    <div style={{ border: '1px solid #eee', padding: 20, backgroundColor: '#fff' }}>
                      <span style={{ color: 'rgba(0, 0, 0, 0.427)', fontSize: 15 }}>
                        金币获取量
                      </span>
                      <h1>{this.state.todayData.coinStat.coinNum}枚</h1>
                    </div>
                  </Col>

                  <Col className="gutter-row" style={{ width: '25%' }}>
                    <div style={{ border: '1px solid #eee', padding: 20, backgroundColor: '#fff' }}>
                      <span style={{ color: 'rgba(0, 0, 0, 0.427)', fontSize: 15 }}>
                        兑换商品总数
                      </span>
                      <h1>{this.state.todayData.exchangeOrderStat.orderNum}</h1>
                    </div>
                  </Col>

                  <Col className="gutter-row" style={{ width: '25%' }}>
                    <div style={{ border: '1px solid #eee', padding: 20, backgroundColor: '#fff' }}>
                      <span style={{ color: 'rgba(0, 0, 0, 0.427)' }}>兑换商品总币数</span>
                      <h1>{this.state.todayData.exchangeOrderStat.totalNum}枚</h1>
                    </div>
                  </Col>
                </Row>
                <Row gutter={18} style={{ marginTop: 20 }}>
                  <Col className="gutter-row" span={24}>
                    <div style={{ backgroundColor: '#fff', padding: 24 }}>
                      <Form ref={this.formRef} onFinish={this.onFinish}>
                        <Row justify="space-between" align="middle" gutter={16}>
                          <Col>
                            <h3 style={{ margin: 0 }}>场地打卡量排行榜</h3>
                          </Col>
                          {/* <Col style={{ display: 'flex', justifyContent: 'flex-end' }}>
                            <Form.Item name="orderDate" style={{ margin: 0 }}>
                              <RangePicker format="YYYY-MM-DD" onChange={this.onChange} />
                            </Form.Item>
                          </Col> */}
                        </Row>
                      </Form>
                      <div
                        style={{
                          height: 1,
                          backgroundColor: '#f0f0f0',
                          margin: '12px 4px 12px 0',
                        }}
                      />
                      <Table
                        dataSource={this.state.sevenDays}
                        columns={columns}
                        pagination={false}
                        scroll={{ y: 800 }}
                      />
                    </div>
                  </Col>
                </Row>
                {/* <div
                  style={{
                    width: 11,
                    height: 11,
                    borderRadius: '50%',
                    backgroundColor: '#9df2ed',
                    position: 'relative',
                    top: '12px',
                    left: '-2px',
                    zIndex: 1,
                  }}
                />
                <div
                  style={{
                    width: 11,
                    height: 11,
                    borderRadius: '50%',
                    backgroundColor: '#6395f9',
                    position: 'relative',
                    top: '1px',
                    left: '86px',
                    zIndex: 1,
                  }}
                /> */}
                {/* <DualAxes {...config} /> */}
              </div>

              {/* <div style={{ backgroundColor: '#fff', padding: 24, marginTop: 20 }}>
                <h3 style={{ marginBottom: 0 }}>平台使用情况</h3>
                <Divider style={{ margin: '20px 0px' }} />
                <Row>
                  <Col span={24}>
                    <div
                      style={{ position: 'absolute', right: '-2px', zIndex: 2, marginTop: '-7px' }}
                    >
                      <span
                        className={this.state.timeType == 'week' ? 'uu' : 'uuu'}
                        onClick={() => this.flowSearch('week')}
                      >
                        本周
                      </span>
                      <span
                        className={this.state.timeType == 'month' ? 'uu' : 'uuu'}
                        onClick={() => this.flowSearch('month')}
                      >
                        本月
                      </span>
                      <span
                        className={this.state.timeType == 'year' ? 'uu' : 'uuu'}
                        onClick={() => this.flowSearch('year')}
                      >
                        全年
                      </span>
                      <span className="uuu" onClick={() => this.flowSearch('custom')}>
                        <RangePicker format="YYYY-MM-DD" onChange={this.onChangevv} />
                      </span>
                    </div>
                  </Col>
                </Row>

                <Row>
                  <Col span={3} style={{ marginTop: 0 }}>
                    <p
                      style={{ marginBottom: 0, fontSize: 14, color: '#929292', marginTop: '-5px' }}
                    >
                      <b>平台用户数</b>
                    </p>
                    <p style={{ marginBottom: 0 }}>
                      <span style={{ fontSize: 28 }}>{userStat.total_num}</span>人
                    </p>
                    <p style={{ marginBottom: 0, color: '#929292' }}>
                      较昨日&nbsp;
                      {Number(userStat.diff_num) > 0 || Number(userStat.diff_num) == 0 ? (
                        <span style={{ color: '#2FC25B' }}>▲</span>
                      ) : (
                        <span style={{ color: 'red' }}>▼</span>
                      )}
                      &nbsp; {Number(userStat.diff_num)}&nbsp;人
                    </p>

                    <p style={{ marginBottom: 0, marginTop: 50, fontSize: 14, color: '#929292' }}>
                      <b>用户活跃度</b>
                    </p>
                    <p style={{ marginBottom: 0 }}>
                      <span style={{ fontSize: 28 }}>{activeStat.total_visit_pv}pv</span>
                    </p>
                    <p style={{ marginBottom: 0, color: '#929292' }}>
                      周同比&nbsp;
                      {Number(activeStat.ratio) > 0 ? (
                        <span style={{ color: '#2FC25B' }}>▲</span>
                      ) : (
                        <span style={{ color: 'red' }}>▼</span>
                      )}
                      &nbsp;{Number(activeStat.ratio).toFixed(2)}&nbsp;％
                    </p>
                  </Col>

                  <Col span={21}>
                    <h3
                      style={{
                        marginBottom: 0,
                        position: 'relative',
                        top: '-29px',
                        left: '0px',
                        zIndex: 1,
                      }}
                    >
                      近期平台流量一览
                    </h3>
                    <div
                      style={{
                        width: 11,
                        height: 11,
                        borderRadius: '50%',
                        backgroundColor: '#6395f9',
                        position: 'relative',
                        top: '-24px',
                        left: '149px',
                        zIndex: 1,
                      }}
                    />
                    <div
                      style={{
                        width: 11,
                        height: 11,
                        borderRadius: '50%',
                        backgroundColor: '#84d6ad',
                        position: 'relative',
                        top: '-35px',
                        left: '226px',
                        zIndex: 1,
                      }}
                    />
                    <DualAxes {...configs} style={{ marginTop: '-50px' }} />
                  </Col>
                </Row>
              </div> */}

              {/* <Row gutter={18} style={{ marginTop: 20 }}>
                <Col className="gutter-row" span={24}>
                  <div style={{ backgroundColor: '#fff', padding: 24 }}>
                    <Form ref={this.formRef} onFinish={this.onFinish}>
                      <Row justify="space-between" align="middle" gutter={16}>
                        <Col>
                          <h3 style={{ margin: 0 }}>门店订单完成情况</h3>
                        </Col>
                        <Col style={{ display: 'flex', justifyContent: 'flex-end' }}>
                          <Form.Item name="orderDate" style={{ margin: 0 }}>
                            <RangePicker format="YYYY-MM-DD" onChange={this.onChange} />
                          </Form.Item>
                        </Col>
                      </Row>
                    </Form>
                    <div
                      style={{
                        height: 1,
                        backgroundColor: '#f0f0f0',
                        margin: '12px 4px 12px 0',
                      }}
                    />
                    <Table
                      dataSource={VenueRanking}
                      columns={columns}
                      pagination={false}
                      loading={this.state.spinning}
                    />
                  </div>
                </Col>
              </Row> */}
            </div>
          </PageContainer>
        </div>
      </div>
    );
  }
}

export default connect()(Loginx);
