import {
  exportMonitorBusinessEquipment,
  getMonitorActiveStat,
  getMonitorBusinessEquipment,
  getMonitorOrderData,
  getMonitorUserStat,
  getMonitorVisitTrendStat,
} from '@/services/monitor';
import { DualAxes } from '@ant-design/plots';
import { PageContainer } from '@ant-design/pro-layout';
import { Button, Col, DatePicker, Divider, message, Row, Table } from 'antd';
import dayjs from 'dayjs';
import moment from 'moment';
import React from 'react';
const { RangePicker } = DatePicker;

class Loginx extends React.Component {
  state = {
    spinning: false,
    timeType: 'today',
    todayData: {},
    // 新增：控制排序状态
    sortedInfo: null,
    // 新增：保存原始数据，用于取消排序时恢复
    originalVenueRanking: [],
  };

  componentDidMount() {
    this.setState(
      {
        orderStartDate: moment().startOf('day').format('YYYY-MM-DD 00:00:00'),
        orderEndDate: moment().endOf('day').format('YYYY-MM-DD 23:59:59'),
      },
      () => {
        this.getData();
        this.getDatax();
        this.getFlow();
        this.getUserStat();
        this.getActiveStat();
      },
    );
  }

  getData = async () => {
    const res = await getMonitorOrderData();
    this.setState({ spinning: false });
    if (res && res.code === 10000) {
      this.setState({ todayData: res.data });
    } else {
      message.error(res?.msg);
    }
  };

  getUserStat = async () => {
    const res = await getMonitorUserStat();
    if (res && res.code === 10000) {
      this.setState({ userStat: res.data });
    } else {
      message.error(res?.msg);
    }
  };

  getActiveStat = async () => {
    const res = await getMonitorActiveStat();
    if (res && res.code === 10000) {
      const activeStat = res.data;
      const plus =
        Number(activeStat.lastWeekVisitPv) === 0
          ? 0
          : (Number(activeStat.weekVisitPv) -
              Number(activeStat.lastWeekVisitPv)) /
            Number(activeStat.lastWeekVisitPv);
      this.setState({
        activeStat,
        plus: isNaN(plus) ? 0 : plus * 100,
      });
    } else {
      message.error(res?.msg);
    }
  };

  getFlow = async () => {
    const params = {
      timeDimension: 'month', // 默认展示近一个月（或者本月）的趋势，因为微信数据是T-1的，选today必定为0
    };
    const res = await getMonitorVisitTrendStat(params);
    this.setState({ spinning: false });
    if (res && res.code === 10000) {
      const statSs = [];
      (res.data || []).forEach((item) => {
        let displayTime = item.dataGroup;
        // 如果是全年数据，后端返回 YYYY-MM，前端展示 月份
        if (
          params.timeDimension === 'year' &&
          displayTime &&
          /^\d{4}-\d{2}$/.test(displayTime)
        ) {
          const m = displayTime.split('-')[1];
          displayTime = `${Number(m)}月`;
        }
        const obj = {
          time: displayTime,
          浏览量: item.totalVisitPv || 0,
          访客数: item.totalVisitUv || 0,
        };
        statSs.push(obj);
      });
      this.setState({ statS: statSs });
    } else {
      message.error(res?.msg);
    }
  };

  getDatax = async () => {
    const res = await getMonitorBusinessEquipment({
      startTime: this.state.orderStartDate,
      endTime: this.state.orderEndDate,
      timeDimension: this.state.timeType,
    });
    this.setState({ spinning: false });
    if (res && res.code === 10000) {
      const newData = res.data || [];
      // 重置排序状态，使用接口返回的原始顺序
      this.setState({
        VenueRanking: newData,
        originalVenueRanking: newData,
        sortedInfo: null,
      });
    } else {
      message.error(res?.msg);
    }
  };

  handleTableChange = (pagination, filters, sorter) => {
    // 更新排序状态（用于控制表头图标）
    this.setState({ sortedInfo: sorter });

    // 如果 sorter.order 存在，则按照该规则排序
    if (sorter.order) {
      const { originalVenueRanking } = this.state;
      const sortedData = [...originalVenueRanking].sort((a, b) => {
        const field = sorter.field || sorter.columnKey; // 兼容不同版本的字段名
        let valA = a[field];
        let valB = b[field];

        // 设备编号通常为字符串，使用字符串比较
        if (field === 'serialNumber') {
          valA = valA || '';
          valB = valB || '';
          return sorter.order === 'ascend'
            ? valA.localeCompare(valB)
            : valB.localeCompare(valA);
        }

        // 其他数字列
        valA = Number(valA) || 0;
        valB = Number(valB) || 0;
        return sorter.order === 'ascend' ? valA - valB : valB - valA;
      });
      this.setState({ VenueRanking: sortedData });
    } else {
      // 取消排序，恢复原始数据
      this.setState({ VenueRanking: this.state.originalVenueRanking });
    }
  };

  handleExport = async () => {
    const params = {
      startTime: this.state.orderStartDate,
      endTime: this.state.orderEndDate,
      timeDimension: this.state.timeType,
    };
    try {
      const res = await exportMonitorBusinessEquipment(params);
      if (res && res.error) {
        message.error(res.msg || '导出失败');
        return;
      }
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' });
      const objectUrl = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = objectUrl;
      const exportTime = moment().format('YYYY-MM-DD_HHmmss');
      a.download = `终端经营情况_${exportTime}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(objectUrl);
      message.success('导出成功');
    } catch (error) {
      console.error('导出错误:', error);
      message.error('导出失败');
    }
  };

  onFinish = () => {
    this.handleExport();
  };

  onChange = (value, dateString) => {
    this.setState(
      {
        orderStartDate: dateString[0] !== '' ? `${dateString[0]}` : undefined,
        orderEndDate: dateString[1] !== '' ? `${dateString[1]}` : undefined,
        spinning: true,
      },
      () => {
        this.getDatax();
      },
    );
  };

  flowSearch = (v) => {
    let startDate, endDate;
    switch (v) {
      case 'today':
        startDate = moment().startOf('day').format('YYYY-MM-DD 00:00:00');
        endDate = moment().endOf('day').format('YYYY-MM-DD 23:59:59');
        break;
      case 'week':
        startDate = moment().startOf('week').format('YYYY-MM-DD 00:00:00');
        endDate = moment().endOf('week').format('YYYY-MM-DD 23:59:59');
        break;
      case 'month':
        startDate = moment().startOf('month').format('YYYY-MM-DD 00:00:00');
        endDate = moment().endOf('month').format('YYYY-MM-DD 23:59:59');
        break;
      case 'year':
        startDate = moment().startOf('year').format('YYYY-MM-DD 00:00:00');
        endDate = moment().endOf('year').format('YYYY-MM-DD 23:59:59');
        break;
      case 'custom':
        startDate = this.state.orderStartDate;
        endDate = this.state.orderEndDate;
        break;
      default:
        return;
    }
    this.setState(
      {
        timeType: v,
        orderStartDate: v !== 'custom' ? startDate : this.state.orderStartDate,
        orderEndDate: v !== 'custom' ? endDate : this.state.orderEndDate,
      },
      () => {
        if (v !== 'custom') {
          this.getDatax();
        }
      },
    );
  };

  render() {
    const {
      VenueRanking = [],
      statS = [],
      userStat = {},
      activeStat = {},
      sortedInfo,
    } = this.state;

    const routes = [
      {
        path: `/`,
        breadcrumbName: '',
      },
    ];

    const configs = {
      tooltip: {
        customContent: (title, data) => {
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
                <span
                  style={{
                    color: data?.length > 0 ? data[0]?.color : '#5B8FF9',
                    marginRight: 10,
                    fontSize: 20,
                    width: 20,
                  }}
                >
                  ●
                </span>
                <span>{data?.length > 0 && data[0]?.name}：</span>
                <span style={{ float: 'right', marginTop: 5 }}>
                  {data?.length > 0 && data[0]?.value}
                </span>
              </div>
              <div style={{ width: '100%', clear: 'both' }}>
                <span
                  style={{
                    color: data?.length > 1 ? data[1]?.color : '#5AD8A6',
                    marginRight: 10,
                    fontSize: 20,
                    width: 20,
                  }}
                >
                  ●
                </span>
                <span>{data?.length > 1 && data[1]?.name}：</span>
                <span style={{ float: 'right', marginTop: 5 }}>
                  {data?.length > 1 && data[1]?.value}
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
      appendPadding: [35, 0, 0, 0],
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
          },
        },
      ],
      yAxis: {
        浏览量: {
          grid: {
            line: {
              style: {
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
        offsetY: Number(10),
      },
    };

    const columns = [
      {
        title: '序号',
        dataIndex: 'name',
        width: 60,
        render: (text, record, inx) => {
          return <div>{inx + 1}</div>;
        },
      },
      {
        title: '备注',
        dataIndex: 'remark',
      },
      {
        title: '所属商超',
        dataIndex: 'supermarketName',
      },
      {
        title: '终端设备',
        dataIndex: 'serialNumber',
        sorter: true,
        sortOrder:
          sortedInfo?.field === 'serialNumber' ? sortedInfo.order : null,
      },
      {
        title: '订单数',
        dataIndex: 'orderNum',
        sorter: true,
        sortOrder: sortedInfo?.field === 'orderNum' ? sortedInfo.order : null,
      },
      {
        title: '待发货订单数',
        dataIndex: 'pendingDeliveryNum',
        sorter: true,
        sortOrder:
          sortedInfo?.field === 'pendingDeliveryNum' ? sortedInfo.order : null,
      },
      {
        title: '已完成订单数',
        dataIndex: 'completedNum',
        sorter: true,
        sortOrder:
          sortedInfo?.field === 'completedNum' ? sortedInfo.order : null,
      },
      {
        title: '订单收入',
        dataIndex: 'orderIncome',
        render: (text) => <div>{Number(text || 0) / 100}</div>,
        sorter: true,
        sortOrder:
          sortedInfo?.field === 'orderIncome' ? sortedInfo.order : null,
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
            <div style={{ backgroundColor: 'transparent' }}>
              <Row gutter={24} style={{ marginBottom: 20 }}>
                <Col className="gutter-row" style={{ width: '25%' }}>
                  <div
                    style={{
                      border: '1px solid #eee',
                      padding: 20,
                      backgroundColor: '#fff',
                    }}
                  >
                    <span style={{ color: '#ccc' }}>今日已完成订单总数</span>
                    <h1>{this.state.todayData.todayOrderNum || 0}</h1>
                  </div>
                </Col>
                <Col className="gutter-row" style={{ width: '25%' }}>
                  <div
                    style={{
                      border: '1px solid #eee',
                      padding: 20,
                      backgroundColor: '#fff',
                    }}
                  >
                    <span style={{ color: '#ccc' }}>今日订单总金额</span>
                    <h1>{this.state.todayData.todayTotalAmount / 100 || 0}</h1>
                  </div>
                </Col>
                <Col className="gutter-row" style={{ width: '25%' }}>
                  <div
                    style={{
                      border: '1px solid #eee',
                      padding: 20,
                      backgroundColor: '#fff',
                    }}
                  >
                    <span style={{ color: '#ccc' }}>累计订单总数</span>
                    <h1>{this.state.todayData.allOrderNum || 0}</h1>
                  </div>
                </Col>
                <Col className="gutter-row" style={{ width: '25%' }}>
                  <div
                    style={{
                      border: '1px solid #eee',
                      padding: 20,
                      backgroundColor: '#fff',
                    }}
                  >
                    <span style={{ color: '#ccc' }}>累计订单总金额</span>
                    <h1>{this.state.todayData.allTotalAmount / 100 || 0}</h1>
                  </div>
                </Col>
              </Row>

              <div
                style={{ backgroundColor: '#fff', padding: 24, marginTop: 20 }}
              >
                <h3 style={{ marginBottom: 0 }}>平台使用情况</h3>
                <Divider style={{ margin: '20px 0px' }} />
                <Row>
                  <Col span={3} style={{ marginTop: 0 }}>
                    <p
                      style={{
                        marginBottom: 0,
                        fontSize: 14,
                        color: '#929292',
                        marginTop: '-5px',
                      }}
                    >
                      <b>平台用户数</b>
                    </p>
                    <p style={{ marginBottom: 0 }}>
                      <span style={{ fontSize: 28 }}>{userStat.totalNum}</span>
                      人
                    </p>
                    <p style={{ marginBottom: 0, color: '#929292' }}>
                      较昨日&nbsp;
                      {Number(userStat.totalNum) >
                      Number(userStat.yesterdayNum) ? (
                        <span style={{ color: '#2FC25B' }}>▲</span>
                      ) : (
                        <span style={{ color: 'red' }}>▼</span>
                      )}
                      &nbsp;{' '}
                      {Math.abs(
                        Number(userStat.totalNum) -
                          Number(userStat.yesterdayNum),
                      )}
                      &nbsp;人
                    </p>
                    <p
                      style={{
                        marginBottom: 0,
                        marginTop: 50,
                        fontSize: 14,
                        color: '#929292',
                      }}
                    >
                      <b>用户活跃度</b>
                    </p>
                    <p style={{ marginBottom: 0 }}>
                      <span style={{ fontSize: 28 }}>
                        {activeStat.totalVisitPv}pv
                      </span>
                    </p>
                    <p style={{ marginBottom: 0, color: '#929292' }}>
                      周同比&nbsp;
                      {Number(activeStat.weekVisitPv) >
                      Number(activeStat.lastWeekVisitPv) ? (
                        <span style={{ color: '#2FC25B' }}>▲</span>
                      ) : (
                        <span style={{ color: 'red' }}>▼</span>
                      )}
                      &nbsp;{this.state.plus}&nbsp;％
                    </p>
                  </Col>
                  <Col span={21} style={{ position: 'relative' }}>
                    <h3
                      style={{
                        marginBottom: 0,
                        position: 'absolute',
                        top: '-5px',
                        left: '0px',
                        zIndex: 1,
                      }}
                    >
                      近期平台流量一览
                    </h3>
                    <DualAxes {...configs} style={{ marginTop: '-20px' }} />
                  </Col>
                </Row>
              </div>

              <Row gutter={18} style={{ marginTop: 20 }}>
                <Col className="gutter-row" span={24}>
                  <div style={{ backgroundColor: '#fff', padding: 24 }}>
                    <div>
                      <Row justify="space-between" align="middle" gutter={16}>
                        <Col>
                          <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                            终端经营情况
                          </h1>
                        </Col>
                        <Col
                          style={{
                            display: 'flex',
                            gap: 10,
                            alignItems: 'center',
                          }}
                        >
                          <div>
                            <span
                              className={
                                this.state.timeType === 'today' ? 'uu' : 'uuu'
                              }
                              onClick={() => this.flowSearch('today')}
                            >
                              今日
                            </span>
                            <span
                              className={
                                this.state.timeType === 'month' ? 'uu' : 'uuu'
                              }
                              onClick={() => this.flowSearch('month')}
                            >
                              本月
                            </span>
                            <span
                              className={
                                this.state.timeType === 'year' ? 'uu' : 'uuu'
                              }
                              onClick={() => this.flowSearch('year')}
                            >
                              全年
                            </span>
                            <span
                              className={
                                this.state.timeType === 'custom' ? 'uu' : 'uuu'
                              }
                              onClick={() => this.flowSearch('custom')}
                            >
                              自定义
                            </span>
                          </div>
                          <DatePicker.RangePicker
                            format="YYYY-MM-DD HH:mm:ss"
                            showTime
                            allowClear={false}
                            onChange={this.onChange}
                            value={[
                              this.state.orderStartDate
                                ? dayjs(this.state.orderStartDate)
                                : undefined,
                              this.state.orderEndDate
                                ? dayjs(this.state.orderEndDate)
                                : undefined,
                            ]}
                          />
                          <Button
                            variant="outlined"
                            onClick={this.handleExport}
                          >
                            导出
                          </Button>
                        </Col>
                      </Row>
                    </div>
                    <div
                      style={{
                        height: 1,
                        backgroundColor: '#f0f0f0',
                        margin: '12px 4px 12px 0',
                      }}
                    />
                    <Table
                      rowKey="serialNumber"
                      dataSource={VenueRanking}
                      columns={columns}
                      pagination={false}
                      loading={this.state.spinning}
                      scroll={{ y: VenueRanking.length > 10 ? 500 : undefined }}
                      onChange={this.handleTableChange}
                    />
                  </div>
                </Col>
              </Row>
            </div>
          </PageContainer>
        </div>
      </div>
    );
  }
}

export default Loginx;
