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
  Progress,
  Tooltip,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
const { TabPane } = Tabs;
import ReactDOM from 'react-dom';
import { DualAxes, Radar, Column, Area, TinyArea } from '@ant-design/plots';

import { each, groupBy } from '@antv/util';
import { InfoCircleOutlined, EllipsisOutlined } from '@ant-design/icons';
import { getUserInformation } from '@/utils/authority';
import styles from './index.less';
const { Option } = Select;
const { RangePicker } = DatePicker;
import moment from 'moment';
import tb from '../../assets/tb.png';
class Loginx extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    spinnings: false,
    timeTypes: 'month',
    stadiumID: undefined,
    LocationType: 2,
    sss: [],
    qqq: [],
    ll: false,
    portraits: [],
  };

  componentDidMount() {
    // window.localStorage.getItem('UserInformation')
    // console.log( window.localStorage.getItem('UserInformation'))

    this.setState(
      {
        manages: JSON.parse(window.localStorage.getItem('UserInformation')),
        todayStart: new Date(new Date().toLocaleDateString()).getTime(), //今天开始，
        todayStart: new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1, //今天结束
        weekStart:
          new Date(new Date().toLocaleDateString()).getTime() -
          (new Date().getDay() - 1) * 24 * 60 * 60 * 1000, //本周开始
        weekEnd:
          new Date(new Date().toLocaleDateString()).getTime() -
          (new Date().getDay() - 1) * 24 * 60 * 60 * 1000, //本周结束

        monthStart: new Date(new Date().getFullYear(), new Date().getMonth(), 1).getTime(), //本月开始
        monthEnd:
          new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).getTime() +
          24 * 60 * 60 * 1000 -
          1, //本月结束
      },
      () => {
        this.getIncome();
      },
    );

    // this.getIncome();
    // this.getDataxx();
  }

  getIncome = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        // 场所列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            type: this.state.LocationType,
            limit: 9999,
          },
          url: `/api/admin/stadium/lists`,
          method: 'GET',
          myData: (res) => {
            if (res && res.code === 200) {
              this.setState({
                lists: res.data.lists,
              });
            } else {
              message.error(res.msg);
            }
          },
        });

        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            stadium_type: this.state.LocationType,
            start_time: this.state.monthStart / 1000,
            end_time: this.state.monthEnd / 1000,
            stadium_id: this.state.stadiumID,
          },
          url: `/api/admin/stadium/stat`,
          method: 'GET',
          myData: (res) => {
            console.log(res, 1111);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              this.setState(
                {
                  basic: res.data.basic, //// 已核销订单数
                  used_info: res.data.used_info,
                  stadium_times: res.data.stadium_times,
                  gym_times: res.data.gym_times,
                  portrait: res.data.portrait,
                },
                () => {
                  const x = this.state.used_info;
                  for (let key in this.state.used_info) {
                    console.log(key);
                    x[key].time = key;
                    x[key]['预定订单数'] = x[key].reserved_num;
                    x[key]['核销入场数'] = x[key].checked_num;
                    x[key]['提前'] = x[key].ahead_num;
                    x[key]['按时'] = x[key].on_time_num;
                    x[key]['异常'] = x[key].over_num;
                  }

                  const { portrait } = this.state;

                  const xx = [];
                  for (let i = 0; i <= 4; i++) {
                    ['teenager', 'young', 'middle', 'old'].map((res) => {
                      xx.push({
                        user:
                          res == 'teenager'
                            ? '青少年'
                            : res == 'young'
                            ? '青年'
                            : res == 'middle'
                            ? '中年'
                            : '老年',
                        item:
                          i == 0
                            ? '中午'
                            : i == 1
                            ? '下午'
                            : i == 2
                            ? '晚上'
                            : i == 3
                            ? '早晨'
                            : i == 4
                            ? '上午'
                            : '',
                        score:
                          i == 0
                            ? portrait[res].noon 
                            : i == 1
                            ? portrait[res].afternoon
                            : i == 2
                            ? portrait[res].night 
                            : i == 3
                            ? portrait[res].morning
                            : portrait[res].early_morning,
                      });
                    });
                  }
                  console.log(xx);

                  this.setState(
                    {
                      portraits: xx,
                      used_infos: Object.values(x),
                    },
                    () => {
                      this.setState({
                        ll: true,
                      });
                      console.log(this.state.portraits);
                    },
                  );
                },
              );
            } else {
              message.error(res.msg);
            }
          },
        });
      },
    );
  };

  getDataxx = () => {
    //场所使用人次排行
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        income_start_date: this.state.orderStartDates,
        income_end_date: this.state.orderEndDates,
      },
      url: `/api/admin/system/income/rank`,
      method: 'GET',
      myData: (res) => {
        this.setState({
          spinnings: false,
        });
        if (res && res.code === 200) {
          this.setState({
            VenueRankings: res.data,
          });
          // message.success(res.message);
        } else {
          message.error(res.message);
        }
      },
    });
  };

  flowSearchs = (v) => {
    if (v == 'today') {
      this.setState(
        {
          monthStart: new Date(new Date().toLocaleDateString()).getTime(), //今天开始，
          monthEnd: new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1, //今天结束
        },
        () => {
          this.getIncome();
        },
      );
    }

    if (v == 'week') {
      this.setState(
        {
          monthStart:
            new Date(new Date().toLocaleDateString()).getTime() -
            (new Date().getDay() - 1) * 24 * 60 * 60 * 1000, //本周开始
          monthEnd:
            new Date(new Date().toLocaleDateString()).getTime() -
            (new Date().getDay() - 8) * 24 * 60 * 60 * 1000, //本周结束
        },
        () => {
          this.getIncome();
        },
      );
    }

    if (v == 'month') {
      this.setState(
        {
          monthStart: new Date(new Date().getFullYear(), new Date().getMonth(), 1).getTime(), //本月开始
          monthEnd:
            new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0).getTime() +
            24 * 60 * 60 * 1000 -
            1, //本月结束
        },
        () => {
          this.getIncome();
        },
      );
    }

    this.setState({
      timeTypes: v,
    });
  };

  onChangevvv = (value, dateString) => {
    console.log(dateString);
    this.setState(
      {
        start_times: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
        end_times: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
      },
      () => {
        var date = new Date(this.state.start_times);
        var time1 = date.getTime();

        var dates = new Date(this.state.end_times);
        var time2 = dates.getTime();

        if (this.state.start_times == undefined) {
          this.setState(
            {
              monthStart: new Date(new Date().toLocaleDateString()).getTime(), //今天开始，
              monthEnd:
                new Date(new Date().toLocaleDateString()).getTime() + 24 * 60 * 60 * 1000 - 1, //今天结束
              timeTypes: 'today',
            },
            () => {
              this.getIncome();
            },
          );
        } else {
          this.setState(
            {
              monthStart: time1,
              monthEnd: time2,
            },
            () => {
              this.getIncome();
            },
          );
        }
      },
    );
  };

  handleChange = (value) => {
    this.setState(
      {
        LocationType: value,
      },
      () => {
        this.getIncome();
      },
    );
  };

  handleChanges = (value) => {
    console.log(value);
    this.setState(
      {
        stadiumID: value,
      },
      () => {
        this.getIncome();
      },
    );
  };

  render() {
    // const portrait = {
    //   teenager: {
    //     // 青少年
    //     early_morning: 0, // 早上
    //     morning: 0, // 上午
    //     noon: 0, // 中午
    //     afternoon: 0, // 下午
    //     night: 0, // 晚上
    //   },
    //   young: {
    //     // 青年
    //     early_morning: 0,
    //     morning: 0,
    //     noon: 0,
    //     afternoon: 0,
    //     night: 0,
    //   },
    //   middle: {
    //     // 中年
    //     early_morning: 0,
    //     morning: 0,
    //     noon: 0,
    //     afternoon: 0,
    //     night: 0,
    //   },
    //   old: {
    //     // 老年
    //     early_morning: 0,
    //     morning: 0,
    //     noon: 0,
    //     afternoon: 0,
    //     night: 0,
    //   },
    // };
    const {
      basic = {},
      used_infos = [],
      portrait = {},
      stadium_times = [],
      gym_times = [],
      lists = [],
      manages = {},
    } = this.state;
    const { teenager = {}, young = {}, middle = {}, old = {} } = portrait;

    const teenagers =
      teenager.early_morning +
      teenager.morning +
      teenager.noon +
      teenager.afternoon +
      teenager.night;
    const youngs = young.early_morning + young.morning + young.noon + young.afternoon + young.night;
    const middles =
      middle.early_morning + middle.morning + middle.noon + middle.afternoon + middle.night;
    const olds = old.early_morning + old.morning + old.noon + old.afternoon + old.night;

    const Totalage = teenagers + youngs + middles + olds;

    console.log(Totalage);

    console.log(used_infos);

    const tq = JSON.parse(JSON.stringify(used_infos));
    const ass = JSON.parse(JSON.stringify(used_infos));
    tq.map((res) => {
      (res.name = '提前'), (res.item = res.time), (res.on_time_num = res.ahead_num);
    });
    console.log(tq);

    ass.map((res) => {
      (res.name = '按时'), (res.item = res.time), (res.on_time_num = res.on_time_num);
    });

    var yc = JSON.parse(JSON.stringify(used_infos));
    yc.map((res) => {
      (res.name = '异常'), (res.item = res.time), (res.on_time_num = res.over_num);
    });

    var cxs = [...tq, ...ass, ...yc];
 
    const config = {
      data: [used_infos, used_infos],
      isStack: true,
      xField: 'time',
      yField: ['预定订单数', '核销入场数'],
      seriesField: 'type',
      animation: false,

      geometryOptions: [
        {
          geometry: 'column',
          minColumnWidth: 5,
          maxColumnWidth: 60,
        },
        {
          geometry: 'line',
          lineStyle: {
            lineWidth: 2,
          },
        },
      ],

      yAxis: {
        // 格式化左坐标轴
        value: {
          grid: {
            line: {
              style: {
                stroke: 'black',
                lineWidth: 2,
                lineDash: [4, 5],
                strokeOpacity: 0.7,
                shadowColor: 'black',
                shadowBlur: 10,
                shadowOffsetX: 5,
                shadowOffsetY: 5,
                cursor: 'pointer',
              },

              // style: {
              //   // stroke: '#d5d5d5',
              //   lineWidth: 1,
              //   lineDash: [7, 4],
              //   strokeOpacity: 0.5,
              //   shadowColor: '#d5d5d5',
              // },
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


console.log(this.state.portraits)






    // 雷达图
    const configs = {
      data: this.state.portraits,
      xField: 'item',
      yField: 'score',
      seriesField: 'user',
      // meta: {
      //   score: {
      //     alias: '次',
      //     min: 0,
      //     max: 5,
      //   },
      // },
      xAxis: {
        line: null,
        tickLine: null,
        grid: {
          line: {
            style: {
              lineDash: null,
            },
          },
        },
      },
      yAxis: {
        line: null,
        tickLine: null,
        grid: {
          line: {
            type: 'line',
            style: {
              lineDash: null,
            },
          },
        },
      },
      // 开启辅助点
      point: {
        size: 2,
      },
    };

    // 走势图
    const confige = {
      data: used_infos.map((res) => res.checked_num),
      height: 10,
      autoFit: true,
      smooth: true,
      width: 100,
      xField: 'time',
      yField: '预定订单数',
      xAxis: {
        range: [0, 1],
        grid: {
          line: {
            style: {
              lineWidth: 0,
              color: '#fff',
            },
          },
        },
      },

      yAxis: {
        range: [0, 1], // x 轴的更改，y 轴：yAxis
        grid: {
          line: {
            style: {
              lineWidth: 0,
              color: '#fff',
            },
          },
        },
      },
    };

    const configss = {
      data: cxs,
      isGroup: true,
      xField: 'item',
      yField: 'on_time_num',
      seriesField: 'name',
      minColumnWidth: 5,
      maxColumnWidth: 60,

      /** 设置颜色 */
      color: ['#3aa0ff', '#4ecb73', '#fbd337'],
      marginRatio: 0,
      /** 设置间距 */
      // dodgePadding: 11,
      // label: {
      //   // 可手动配置 label 数据标签位置
      //   position: 'middle',
      //   // 'top', 'middle', 'bottom'
      //   // 可配置附加的布局方法
      //   layout: [

      //     // 柱形图数据标签位置自动调整
      //     {
      //       type: 'interval-adjust-position',
      //     }, // 数据标签防遮挡
      //     {
      //       type: 'interval-hide-overlap',
      //     }, // 数据标签文颜色自动调整
      //     {
      //       type: 'adjust-color',
      //     },
      //   ],
      // },

      yAxis: {
        // x 轴的更改，y 轴：yAxis
        grid: {
          line: {
            style: {
              // stroke: '#d5d5d5',
              // lineWidth: 1,
              // lineDash: [7, 4],
              // strokeOpacity: 0.5,
              // shadowColor: '#d5d5d5',
            },
          },
        },
      },
    };

    // 场所使用人次排行
    const columnss = [
      {
        title: `${this.state.stadiumID ? '场地' : '场所'}`,
        dataIndex: 'name',
      },

      {
        title: '次',
        dataIndex: 'num',
      },
    ];

    return (
      <Spin spinning={this.state.spinning}>
        <div className="qqq">
          <div className="www">
            <PageContainer
              header={{
                title: ``,
                breadcrumb: {
                  itemRender: this.itemRender,
                  // routes,
                },
              }}
            >
              <div style={{ backgroundColor: '#fff', padding: 20, marginBottom: 20 }}>
                <Row gutter={16}>
                  <Col className="gutter-row" span={3}>
                    <Select
                      allowClear
                      placeholder="请选择"
                      style={{ width: '100%' }}
                      onChange={this.handleChange}
                      defaultValue={this.state.LocationType}
                      disabled={manages&&manages.phone == '17507699111' ? false : true}
                    >
                      <Option value={1}>公共场所</Option>
                      <Option value={2}>学校场所</Option>
                      <Option value={3}>社会场所</Option>
                    </Select>
                  </Col>

                  <Col className="gutter-row" span={3}>
                    <Select
                      allowClear
                      placeholder="请选择"
                      style={{ width: '100%' }}
                      onChange={this.handleChanges}
                    >
                      {lists.map((res) => {
                        return (
                          <Option value={res.id} key={res.id}>
                            {res.name}
                          </Option>
                        );
                      })}
                    </Select>
                  </Col>

                  <Col className="gutter-row" span={18}>
                    <div style={{ position: 'absolute', right: 0 }}>
                      <span
                        className={this.state.timeTypes == 'today' ? 'uu' : 'uuu'}
                        onClick={() => this.flowSearchs('today')}
                      >
                        今天
                      </span>
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
                      <span className="uuu" onClick={() => this.flowSearchs('custom')}>
                        <RangePicker format="YYYY-MM-DD" onChange={this.onChangevvv} />
                      </span>
                    </div>
                  </Col>
                </Row>
              </div>
              <div style={{ backgroundColor: '#f0f2f5' }}>
                <Row gutter={24} style={{ marginBottom: 20 }}>
                  <Col className="gutter-row" style={{ width: '25%' }}>
                    <div
                      style={{
                        border: '1px solid #eee',
                        padding: 20,
                        backgroundColor: '#fff',
                        height: 170,
                      }}
                    >
                      <span>已核销入场订单数</span>
                      <div style={{ float: 'right', cursor: 'pointer' }}>
                        <Tooltip placement="top" title="正常核销入场的订单总数">
                          <InfoCircleOutlined />
                        </Tooltip>
                      </div>

                      <h1 style={{ fontSize: 30 }}>{basic.checked_num}</h1>
                      <Row style={{ position: 'absolute', bottom: 20, width: '100%' }}>
                        <Col span={12}>
                          周同比
                          {basic.checked_num /
                            (basic.last_week_checked_num == 0 ? 1 : basic.last_week_checked_num) >
                          0 ? (
                            <>
                              <span style={{ color: 'red' }}>&nbsp;▲&nbsp;</span>
                            </>
                          ) : (
                            <>
                              <span style={{ color: '#8fd96b' }}>&nbsp;▼&nbsp;</span>
                            </>
                          )}
                          {Math.round(
                            (basic.checked_num /
                              (basic.last_week_checked_num == 0
                                ? 1
                                : basic.last_week_checked_num)) *
                              100,
                          )}
                          %{/* {basic.checked_num/basic.last_week_checked_num} */}
                        </Col>
                        <Col span={12}>
                          日环比
                          {/* <span style={{ color: 'red' }}>▲</span> */}
                          {basic.checked_num /
                            (basic.yesterday_checked_num == 0 ? 1 : basic.yesterday_checked_num) >
                          0 ? (
                            <>
                              <span style={{ color: 'red' }}>&nbsp;▲&nbsp;</span>
                            </>
                          ) : (
                            <>
                              <span style={{ color: '#8fd96b' }}>&nbsp;▼&nbsp;</span>
                            </>
                          )}
                          {Math.round(
                            (basic.checked_num /
                              (basic.last_week_checked_num == 0
                                ? 1
                                : basic.yesterday_checked_num)) *
                              100,
                          )}
                          %
                        </Col>
                      </Row>
                    </div>
                  </Col>
                  <Col className="gutter-row" style={{ width: '25%' }}>
                    <div
                      style={{
                        border: '1px solid #eee',
                        padding: 20,
                        backgroundColor: '#fff',
                        height: 170,
                      }}
                    >
                      <span style={{ position: 'relative', zIndex: 2 }}>预订订单数</span>
                      <div style={{ float: 'right', cursor: 'pointer' }}>
                        <Tooltip placement="top" title="有效预订订单数，非退款，非取消">
                          <InfoCircleOutlined />
                        </Tooltip>
                      </div>
                      <h1 style={{ fontSize: 30, position: 'relative', zIndex: 2 }}>
                        {basic.reserved_num}
                      </h1>
                      {/* <div style={{width:30,height:150,zIndex:1,backgroundColor:'#fff',position: "absolute",top:"5px"}} /> */}

                      {/* <img src={tb} style={{ marginTop: 5 }} /> */}
                      <div style={{ height: 80, marginTop: '-35px' }}>
                        {/* <Area {...confige} /> */}

                        <TinyArea {...confige} color="#975fe4" />
                        {/* <div
                          style={{
                            width: '100%',
                            height: 18,
                            backgroundColor: '#fff',
                            marginTop: '-19px',
                            position: 'relative',
                          }}
                        /> */}
                      </div>
                    </div>
                  </Col>

                  <Col className="gutter-row" style={{ width: '25%' }}>
                    <div
                      style={{
                        border: '1px solid #eee',
                        padding: 20,
                        backgroundColor: '#fff',
                        height: 170,
                      }}
                    >
                      <span>核销入场率</span>
                      <div style={{ float: 'right', cursor: 'pointer' }}>
                        <Tooltip placement="top" title="已核销入场订单数与有效预订订单数的比率">
                          <InfoCircleOutlined />
                        </Tooltip>
                      </div>
                      <h1 style={{ fontSize: 30 }}>
                        {basic.reserved_num ? (
                          <>{Math.round((basic.checked_num / basic.reserved_num) * 100)}%</>
                        ) : (
                          '0'
                        )}
                      </h1>
                      <div style={{ position: 'absolute', bottom: 20, width: '80%', zIndex: 2 }}>
                        <Progress
                          percent={Math.round((basic.checked_num / basic.reserved_num) * 100)}
                          showInfo={false}
                          size="small"
                          style={{ marginTop: '-3px' }}
                        />
                      </div>
                      <div className="yy" />
                    </div>
                  </Col>

                  <Col className="gutter-row" style={{ width: '25%' }}>
                    <div
                      style={{
                        border: '1px solid #eee',
                        padding: 20,
                        backgroundColor: '#fff',
                        height: 170,
                      }}
                    >
                      <span>准时离场率</span>
                      <div style={{ float: 'right', cursor: 'pointer' }}>
                        <Tooltip
                          placement="top"
                          title="按时离场和提前离场的数据总和与核销入场总数的比率"
                        >
                          <InfoCircleOutlined />
                        </Tooltip>
                      </div>
                      <h1 style={{ fontSize: 30 }}>
                        {basic.checked_num ? (
                          <>{Math.round((basic.leave_on_time_num / basic.checked_num) * 100)}%</>
                        ) : (
                          '0'
                        )}
                      </h1>
                      <div style={{ position: 'absolute', right: 30, top: 65 }}>
                        <Progress
                          type="circle"
                          showInfo={false}
                          strokeWidth={15}
                          width={80}
                          percent={Math.round((basic.leave_on_time_num / basic.checked_num) * 100)}
                        />
                      </div>
                    </div>
                  </Col>
                </Row>

                <Row>
                  <Col span={15}>
                    <div style={{ backgroundColor: '#fff', padding: 20, marginRight: 20,height:482 }}>
                      <h3 style={{ marginBottom: 15 }}>场所使用情况</h3>
                      <div className="detaileds" style={{ right: 40, top: 15 }}>
                        {/* {manages.phone=='17507699111'? <Link to={`index?status=[200,310,400,510]`}>
                        <EllipsisOutlined />
                      </Link>: <Link to={`runOrder?xq=1`}>
                        <EllipsisOutlined />
                      </Link>} */}

                        <Link
                          to={`runOrder?xq=1&monthStart=${this.state.monthStart}&monthEnd=${this.state.monthEnd}&LocationType=${this.state.LocationType}`}
                        >
                          <EllipsisOutlined style={{ fontSize: 26, color: '#ccc' }} />
                        </Link>
                      </div>


                   
                      <div
                        style={{
                          width: 11,
                          height: 11,
                          borderRadius: '50%',
                          backgroundColor: '#3aa0ff',
                          position: 'relative',
                          top: '11px',
                          left: '0',
                          zIndex: 1,
                        }}
                      />
                      <div
                        style={{
                          width: 11,
                          height: 11,
                          borderRadius: '50%',
                          backgroundColor: '#4ecb73',
                          position: 'relative',
                          top: '-1px',
                          left: '100px',
                          zIndex: 1,
                        }}
                      />
                     

                      <DualAxes {...config} style={{ marginTop: '-20px' }} />
                    </div>
                  </Col>

                  <Col span={9}>
                    <div style={{ backgroundColor: '#fff', padding: 20, height: 482 }}>
                      <h3 style={{ marginBottom: 0 }}>用户群体运动特征画像</h3>
                      <div className="detaileds">
                        <Tooltip
                          placement="left"
                          title={
                            <>
                              <div style={{ width: 150 }}>
                                <div>时间定义</div>
                                <p style={{ marginBottom: 3 }}>早上：(5:00-8:00)</p>
                                <p style={{ marginBottom: 3 }}>上午：(8:00-11:00)</p>
                                <p style={{ marginBottom: 3 }}>中午：(11:00-14:00)</p>
                                <p style={{ marginBottom: 3 }}>下午：(14:00-17:00)</p>
                                <p style={{ marginBottom: 3 }}>晚上：(17:00-22:00)</p>
                              </div>
                              <div style={{ width: 150 }}>
                                <div>年龄定义</div>
                                <p style={{ marginBottom: 3 }}>少年：7-17岁</p>
                                <p style={{ marginBottom: 3 }}>青年：18-40岁</p>
                                <p style={{ marginBottom: 3 }}>中年：41-65岁</p>
                                <p style={{ marginBottom: 3 }}>老年：66岁以上</p>
                              </div>
                            </>
                          }
                        >
                          <InfoCircleOutlined />
                        </Tooltip>
                      </div>
                      <div style={{ height: 330 }}>
                        <div
                          style={{
                            width: '100%',
                            height: 20,
                            backgroundColor: '#fff',
                            position: 'relative',
                            marginBottom: '-20px',
                            zIndex: 1,
                          }}
                        />
                        <Radar {...configs} />
                      </div>

                      <div
                        style={{ width: 300, margin: '0 auto', textAlign: 'center', marginTop: 30 }}
                      >
                        <Row>
                          <Col span={6}>
                            <span
                              style={{
                                color: '#5b8ff9',
                                fontSize: 24,
                                position: 'relative',
                                top: 2,
                              }}
                            >
                              ●
                            </span>{' '}
                            青少年
                            <div>
                              {' '}
                              {teenagers == 0 ? (
                                '0%'
                              ) : (
                                <> {Math.round((Totalage / teenagers) * 100)}%</>
                              )}
                            </div>
                          </Col>
                          <Col span={6}>
                            <span
                              style={{
                                color: '#5ad8a6',
                                fontSize: 24,
                                position: 'relative',
                                top: 2,
                              }}
                            >
                              ●
                            </span>{' '}
                            青年
                            <div>
                              {youngs == 0 ? '0%' : <>{Math.round((Totalage / youngs) * 100)}%</>}
                            </div>
                          </Col>
                          <Col span={6}>
                            <span
                              style={{
                                color: '#5d7092',
                                fontSize: 24,
                                position: 'relative',
                                top: 2,
                              }}
                            >
                              ●
                            </span>{' '}
                            中年
                            <div>
                              {middles == 0 ? '0%' : <>{Math.round((Totalage / middles) * 100)}%</>}
                            </div>
                          </Col>
                          <Col span={6}>
                            <span
                              style={{
                                color: '#f6bd16',
                                fontSize: 24,
                                position: 'relative',
                                top: 2,
                              }}
                            >
                              ●
                            </span>{' '}
                            老年
                            <div>
                              {olds == 0 ? '0%' : <>{Math.round((Totalage / olds) * 100)}%</>}
                            </div>
                          </Col>
                        </Row>
                      </div>
                    </div>
                  </Col>
                </Row>

                <div>
                  <Row>
                    <Col span={15}>
                      <div
                        style={{
                          backgroundColor: '#fff',
                          padding: 20,
                          marginTop: 20,
                          marginRight: 20,
                          height:483
                        }}
                      >
                        <h3 style={{ marginBottom: 15 }}>核销离场分析</h3>
                        <div className="detaileds" style={{ right: 40, top: 35 }}>
                          <Link
                            to={`runOrder?xq=1&xqq=2&monthStart=${this.state.monthStart}&monthEnd=${this.state.monthEnd}&LocationType=${this.state.LocationType}`}
                          >
                            <EllipsisOutlined style={{ fontSize: 26, color: '#ccc' }} />
                          </Link>
                        </div>


                     
                        <div
                          style={{
                            width: 11,
                            height: 11,
                            borderRadius: '50%',
                            backgroundColor: '#3aa0ff',
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
                            backgroundColor: '#4ecb73',
                            position: 'relative',
                            top: '1px',
                            left: '80px',
                            zIndex: 1,
                          }}
                        />
                        <div
                          style={{
                            width: 11,
                            height: 11,
                            borderRadius: '50%',
                            backgroundColor: '#fbd337',
                            position: 'relative',
                            top: '-10px',
                            left: '163px',
                            zIndex: 1,
                          }}
                        />
                       
                       
                        <Column {...configss} style={{ marginTop: '-30px' }} />
                      </div>
                    </Col>
                    <Col span={9}>
                      <div
                        style={{ backgroundColor: '#fff', padding: 20, marginTop: 20, height: 483 }}
                      >
                        <h3 style={{ marginBottom: 15 }}>
                          {this.state.stadiumID ? '场地使用人次排行' : '场所使用人次排行'}
                        </h3>
                        {/* <Table
                          dataSource={this.state.stadiumID ? gym_times : stadium_times}
                          columns={columnss}
                          pagination={false}
                          loading={this.state.spinnings}
                        /> */}

                        <ul className={styles.rankingList}>
                          {(this.state.stadiumID ? gym_times : stadium_times).map((item, i) => (
                            <li key={item.name}>
                              <span
                                className={`${styles.rankingItemNumber} ${
                                  i < 3 ? styles.active : ''
                                }`}
                              >
                                {i + 1}
                              </span>
                              <span className={styles.rankingItemTitle} title={item.name}>
                                {item.name}
                              </span>
                              <span className={styles.rankingItemValue}>
                                {item.num}
                              </span>
                            </li>
                          ))}
                        </ul>
                      </div>
                    </Col>
                  </Row>
                </div>
              </div>
            </PageContainer>
          </div>
        </div>
      </Spin>
    );
  }
}

export default connect()(Loginx);
