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
  InputNumber,
  Menu,
  Dropdown,
  Space,
  Progress,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
import { handleExport } from '../../utils/utils';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import ExportJsonExcel from 'js-export-excel';
import moment from 'moment';
// import NewVenues from './components/NewVenues';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;

class Login extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: Number(this.props.location.query.pageNum ? this.props.location.query.pageNum : 1),
    list: [],
    xxx: false,
    selectedRecord: [],
    aa: [],
    www: [],
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
        //列表
        const params = {
          status_list: this.state.xqq == 2 ? [400] : [200, 310, 400, 510],
          stadium_type: this.state.LocationType,
          page: this.state.pageNum,
          start_time: monthStarts,
          end_time: monthEnds,
        };

        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchField1: this.state.searchField1,
            searchField2: this.state.searchField2,
            searchIntStatus: this.state.searchIntStatus,
            searchField3: this.state.searchField3,
            searchStrField1: this.state.searchStrField1,
            searchStrField2: this.state.searchStrField2,
            keyword: this.state.keyword,
            startTime: this.state.start_time,
            end_time: this.state.end_time,
            pageNum: this.state.pageNum,
          },
          url: `/ddql/order/exchange/lists`,
          method: 'POST',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 10000) {
              this.setState({
                list: res.data.list,
                total: res.data.total,
              });
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });
        //统计
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchField1: this.state.searchField1,
            searchField2: this.state.searchField2,
            searchIntStatus: this.state.searchIntStatus,
            searchField3: this.state.searchField3,
            searchStrField1: this.state.searchStrField1,
            searchStrField2: this.state.searchStrField2,
            keyword: this.state.keyword,
            startTime: this.state.start_time,
            end_time: this.state.end_time,
          },
          url: `/ddql/order/exchange/stat`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 200) {
              console.log(res.data);
              this.setState({
                statistical: res.data,
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

  onSelectChange = (selectedRowKeys, xx) => {
    //触发表单筛选
    this.setState(
      {
        xx,
      },
      () => {
        console.log(this.state.selectedRowKeys);
      },
    );
  };

  pageChange = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNum: page,
        aa: this.state.selectedRowKeys,
        www: this.state.selectedRecord,
      },
      () => {
        this.getData();
      },
    );
  };

  showModal = (add) => {
    if (this.state.selectedRowKeys) {
      this.setState({
        newVenues: true,
        add,
      });
    } else {
      message.error('请先选择要退款的订单');
    }
  };

  handleOk = () => {
    console.log(this.state.selectedRecord);
    this.formRefs.current.validateFields().then((values) => {
      this.state.selectedRecord.map((res) => {
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            id: res.id,
            reason: values.reason, //
            amount: res.amount,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/order/refund`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              this.setState({
                newVenues: false,
              });
              this.getData();
            } else {
              message.error(res.message);
            }
          },
        });
      });
    });
  };

  handleCancel = () => {
    this.setState({
      newVenues: false,
    });
  };

  onFinish = (vas) => {
    console.log(vas);
    this.setState(
      {
        id: vas.id,
        order_no: vas.order_no, //订单编号
        gym_id: vas.gym_id, //场馆
        order_type: vas.order_type, //订单类型
        order_way: vas.order_way, //下单方式
        pay_way: vas.pay_way, //方式支付
        phone: vas.phone && vas.phone.replace(/\s+/g, ''), //下单手机号
        status: vas.status, //订单状态
        ticket_no: vas.ticket_no, //券码
        created_at: vas.created_at, //创建时间
        stadium_id: vas.stadium_id,
        start_time: this.state.start_time,
        end_time: this.state.end_time,
        pageNum: 1,
        leave_status: vas.leave_status,
        selectedRowKeys: [],
        selectedRecord: [],
        www: [],
        aa: [],
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
        order_no: undefined, //订单编号
        gym_id: undefined, //场馆
        order_type: undefined, //订单类型
        order_way: undefined, //下单方式
        pay_way: undefined, //方式支付
        phone: undefined, //下单手机号
        status: undefined, //订单状态
        ticket_no: undefined, //券码
        created_at: undefined, //创建时间
        pageNum: 1,
        start_time: undefined,
        end_time: undefined,
        stadium_id: undefined,
        leave_status: undefined,
        selectedRowKeys: [],
        selectedRecord: [],
        www: [],
        aa: [],
      },
      () => {
        this.getData();
      },
    );
  };

  handleChange = (value) => {
    // //场馆
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        stadium_id: value,
        limit: 999,
      },
      url: `/api/admin/gym/lists`,
      method: 'GET',
      myData: (res) => {
        if (res && res.code === 200) {
          console.log(res.data);
          this.setState({
            gymList: res.data.lists,
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

  aperto = (x) => {
    this.setState({
      xxx: x,
    });
  };

  downloadExcel = () => {
    if (this.state.list.length == 0) {
      message.error('请选择需要操作的订单');
      return;
    }

    message.success('请稍等');
    const params = {
      status_list: this.state.xqq == 2 ? 400 : [200, 310, 400, 510],
      stadium_type: this.state.LocationType,
      page: this.state.pageNum,
      limit: 99999999,
      start_time: this.state.monthStartss,
      end_time: this.state.monthEndss,
    };

    //列表
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        // id: this.state.id,
        // order_no: this.state.order_no, //订单编号
        // gym_id: this.state.gym_id, //场馆
        // order_type: this.state.order_type, //订单类型
        // order_way: this.state.order_way, //下单方式
        // pay_way: this.state.pay_way, //方式支付
        // phone: this.state.phone, //下单手机号
        // status: this.state.status, //订单状态
        // ticket_no: this.state.ticket_no, //券码
        // start_time: this.state.start_time,
        // end_time: this.state.end_time,
        // // created_at: this.state.created_at, //创建时间
        // page: this.state.pageNum,
        // limit: 99999999,
      },
      url: `/api/admin/order/lists?${handleExport(params)}`,
      method: 'GET',
      myData: (res) => {
        console.log(res, this.state.selectedRowKeys);
        this.setState({
          spinning: false,
        });
        if (res && res.code === 200) {
          console.log(res.data.lists);
          this.setState(
            {
              listxx: res.data.lists,
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
                  fmt = fmt.replace(
                    RegExp.$1,
                    (this.getFullYear() + '').substr(4 - RegExp.$1.length),
                  );
                for (var k in o)
                  if (new RegExp('(' + k + ')').test(fmt))
                    fmt = fmt.replace(
                      RegExp.$1,
                      RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length),
                    );
                return fmt;
              };

              const datas = this.state.listxx ? this.state.listxx : ''; //表格数据
              var option = {};
              let dataTable = [];
              if (datas) {
                message.success('导出中');
                for (let i in datas) {
                  const xxxx = `${datas[i].status == 100 ? '待付款' : ''}${
                    datas[i].status == 200 ? '待核销' : ''
                  }${datas[i].status == 400 ? '已完成' : ''}${
                    datas[i].status == 300 ? '退款中' : ''
                  }${datas[i].status == 500 ? '已取消' : ''}${
                    datas[i].status == 600 ? '已退款' : ''
                  }${datas[i].status == 510 ? '已失效' : ''}`;
                  const ss = `${datas[i].order_way == 1 ? '自助下单' : ''}${
                    datas[i].order_way == 2 ? '人工售票' : ''
                  }${datas[i].order_way == 3 ? '后台新增' : ''}`;

                  var theTime = parseInt(datas[i].over_seconds); // 秒
                  var middle = 0; // 分
                  var hour = 0; // 小时

                  if (theTime > 60) {
                    middle = parseInt(theTime / 60);
                    theTime = parseInt(theTime % 60);
                    if (middle > 60) {
                      hour = parseInt(middle / 60);
                      middle = parseInt(middle % 60);
                    }
                  }
                  var result = '' + parseInt(theTime) + '秒';
                  if (middle > 0) {
                    result = '' + parseInt(middle) + '分' + result;
                  }
                  if (hour > 0) {
                    result = '' + parseInt(hour) + '小时' + result;
                  }
                  // console.log(result);

                  let obj = {
                    ID: datas[i].id,
                    订单编号: datas[i].order_no,
                    场馆: datas[i].gym && datas[i].gym.name,
                    订单类型: datas[i].order_type == 1 ? '订场地' : '订门票',
                    下单方式: ss,
                    支付方式: `${datas[i].pay_way == 5 ? '通莞' : ''}${
                      datas[i].pay_way == 1 ? '微信' : ''
                    }${datas[i].pay_way == 2 ? '现金' : ''} `,
                    '订单金额(元)': Number(datas[i].amount),
                    下单手机号: datas[i].phone,
                    订单状态: xxxx,
                    离场状态:
                      datas[i].status == 510 || datas[i].status == 500
                        ? '没进场'
                        : datas[i].over_seconds > 300
                        ? `超时${result}`
                        : datas[i].leave_time
                        ? '已离场'
                        : '待离场',
                    创建时间: datas[i].created_at,
                  };
                  dataTable.push(obj);
                  // }
                }
              }
              // 文件名称
              option.fileName = `订单列表-${new Date().Format('yyyy-MM-dd hh：mm')}`;
              option.datas = [
                {
                  sheetData: dataTable,
                  sheetName: 'sheet',
                  sheetFilter: [
                    'ID',
                    '订单编号',
                    '场馆',
                    '订单类型',
                    '下单方式',
                    '支付方式',
                    '订单金额(元)',
                    '下单手机号',
                    '订单状态',
                    '离场状态',
                    '创建时间',
                  ],
                  sheetHeader: [
                    'ID',
                    '订单编号',
                    '场馆',
                    '订单类型',
                    '下单方式',
                    '支付方式',
                    '订单金额(元)',
                    '下单手机号',
                    '订单状态',
                    '离场状态',
                    '创建时间',
                  ],
                },
              ];

              var toExcel = new ExportJsonExcel(option);
              toExcel.saveExcel();
            },
          );
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  Select = () => {
    if (this.state.selectedRecord.length == 0) {
      message.error('请选择需要操作的订单');
      return;
    }
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

    const datas = this.state.selectedRecord ? this.state.selectedRecord : ''; //表格数据
    var option = {};
    let dataTable = [];
    if (datas) {
      message.success('导出中');
      for (let i in datas) {
        const xxxx = `${datas[i].status == 100 ? '待付款' : ''}${
          datas[i].status == 200 ? '待核销' : ''
        }${datas[i].status == 400 ? '已完成' : ''}${datas[i].status == 300 ? '退款中' : ''}${
          datas[i].status == 500 ? '已取消' : ''
        }${datas[i].status == 600 ? '已退款' : ''}${datas[i].status == 510 ? '已失效' : ''}`;
        const ss = `${datas[i].order_way == 1 ? '自助下单' : ''}${
          datas[i].order_way == 2 ? '人工售票' : ''
        }${datas[i].order_way == 3 ? '后台新增' : ''}`;

        var theTime = parseInt(datas[i].over_seconds); // 秒
        var middle = 0; // 分
        var hour = 0; // 小时

        if (theTime > 60) {
          middle = parseInt(theTime / 60);
          theTime = parseInt(theTime % 60);
          if (middle > 60) {
            hour = parseInt(middle / 60);
            middle = parseInt(middle % 60);
          }
        }
        var result = '' + parseInt(theTime) + '秒';
        if (middle > 0) {
          result = '' + parseInt(middle) + '分' + result;
        }
        if (hour > 0) {
          result = '' + parseInt(hour) + '小时' + result;
        }
        console.log(result);

        let obj = {
          ID: datas[i].id,
          订单编号: datas[i].order_no,
          场馆: datas[i].gym && datas[i].gym.name,
          订单类型: datas[i].order_type == 1 ? '订场地' : '订门票',
          下单方式: ss,
          支付方式: `${datas[i].pay_way == 5 ? '通莞' : ''}${datas[i].pay_way == 1 ? '微信' : ''}${
            datas[i].pay_way == 2 ? '现金' : ''
          } `,
          '订单金额(元)': Number(datas[i].amount),
          下单手机号: datas[i].phone,
          订单状态: xxxx,
          离场状态:
            datas[i].status == 510 || datas[i].status == 500
              ? '没进场'
              : datas[i].over_seconds > 300
              ? `超时${result}`
              : datas[i].leave_time
              ? '已离场'
              : '待离场',
          创建时间: datas[i].created_at,
        };
        dataTable.push(obj);
        // }
      }
    }
    // 文件名称
    option.fileName = `订单列表-${new Date().Format('yyyy-MM-dd hh：mm')}`;
    option.datas = [
      {
        sheetData: dataTable,
        sheetName: 'sheet',
        sheetFilter: [
          'ID',
          '订单编号',
          '场馆',
          '订单类型',
          '下单方式',
          '支付方式',
          '订单金额(元)',
          '下单手机号',
          '订单状态',
          '离场状态',
          '创建时间',
        ],
        sheetHeader: [
          'ID',
          '订单编号',
          '场馆',
          '订单类型',
          '下单方式',
          '支付方式',
          '订单金额(元)',
          '下单手机号',
          '订单状态',
          '离场状态',
          '创建时间',
        ],
      },
    ];

    var toExcel = new ExportJsonExcel(option);
    toExcel.saveExcel();
  };
  render() {
    const {
      gymTypelist = [],
      selectedRowKeys,
      NewRoles,
      total,
      pageNum,
      gymList = [],
      xxx,
      stadiumList = [],
      statistical,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onSelectAll: (selected, selectedRows, changeRows) => {
        console.log(selected, selectedRows, changeRows);
        this.setState({
          selectedRowKeys: selected
            ? selectedRows
                .filter((res) => res)
                .map((res) => res.id)
                .concat(this.state.aa)
            : [],
          selectedRecord: selected
            ? selectedRows
                .filter((res) => res)
                .map((res) => res)
                .concat(this.state.www)
            : [],
        });
      },
      // onChange: this.onSelectChange,
      onSelect: (record, selected, selectedRows, nativeEvent) => {
        if (!selected) {
          this.setState(
            {
              selectedRowKeys: this.state.selectedRowKeys.filter((res) => res != record.id),
              selectedRecord: this.state.selectedRecord.filter((res) => res.id != record.id),
            },
            () => {
              console.log(this.state.selectedRecord);
            },
          );
        } else {
          console.log(record, selected, selectedRows, nativeEvent);
          this.setState(
            {
              selectedRowKeys: [
                ...this.state.aa,
                ...selectedRows.filter((res) => res).map((res) => res.id),
              ],
              selectedRecord: [...this.state.www, ...selectedRows.filter((res) => res)],
            },
            () => {
              console.log(this.state.selectedRecord);
            },
          );
        }
        console.log(record, selected, selectedRows, nativeEvent);
      },
    };

    const menu = (
      <Menu>
        <Menu.Item key="1" onClick={this.downloadExcel}>
          <a>全部</a>
        </Menu.Item>
        <Menu.Item key="2" onClick={this.Select}>
          <a>选中</a>
        </Menu.Item>
      </Menu>
    );

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '订单编号',
        dataIndex: 'order_no',
      },

      {
        title: '场所',
        dataIndex: 'stadium',
        render: (text, record) => {
          return <div>{text.name}</div>;
        },
      },

      {
        title: '场馆',
        render: (text, record) => {
          return <div>{record.gym && record.gym.name}</div>;
        },
      },

      {
        title: '订单类型',
        dataIndex: 'order_type',

        render: (text, record) => {
          return (
            <div>
              <span>{text === 1 ? <span>订场地</span> : <span>订门票</span>}</span>
            </div>
          );
        },
      },

      {
        title: '下单方式',
        dataIndex: 'order_way',
        render: (text, record) => {
          return (
            <div>
              <span>
                {record.order_way == 1 && <span>自助下单</span>}
                {record.order_way == 2 && <span>人工售票</span>}
                {record.order_way == 3 && <span>后台新增</span>}
              </span>
            </div>
          );
        },
      },

      {
        title: '支付方式',
        dataIndex: 'pay_way',
        render: (text, record) => {
          return (
            <div>
              <span>
                {record.pay_way == 1 && <span>微信支付</span>}
                {record.pay_way == 2 && <span>现金支付</span>}
                {record.pay_way == 5 && <span>通莞支付</span>}
              </span>
            </div>
          );
        },
      },

      {
        title: '订单金额(元)',
        dataIndex: 'amount',
      },

      {
        title: '下单手机号',
        dataIndex: 'phone',
      },

      {
        title: '订单状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              {record.status == 100 && <span className="huangse">待付款</span>}
              {record.status == 200 && <a>待核销</a>}
              {record.status == 400 && <span className="luSe">已完成</span>}
              {record.status == 300 && <span style={{ color: 'red' }}>退款中</span>}
              {record.status == 500 && <span style={{ color: '#ccc' }}>已取消</span>}
              {record.status == 600 && <span style={{ color: '#1890ff' }}>已退款</span>}
              {record.status == 510 && <span style={{ color: '#f59a23fe' }}>已失效</span>}
            </div>
          );
        },
      },

      {
        title: '离场状态',
        dataIndex: 'over_seconds',
        render: (text, record) => {
          var theTime = parseInt(record.over_seconds); // 秒
          var middle = 0; // 分
          var hour = 0; // 小时

          if (theTime > 60) {
            middle = parseInt(theTime / 60);
            theTime = parseInt(theTime % 60);
            if (middle > 60) {
              hour = parseInt(middle / 60);
              middle = parseInt(middle % 60);
            }
          }
          var result = '' + parseInt(theTime) + '秒';
          if (middle > 0) {
            result = '' + parseInt(middle) + '分' + result;
          }
          if (hour > 0) {
            result = '' + parseInt(hour) + '小时' + result;
          }
          // console.log(result);
          // return result;

          return (
            <div>
              {record.status == 510 || record.status == 500 ? (
                <span style={{ color: '#ccc' }}>没进场</span>
              ) : (
                <>
                  {record.over_seconds > 300 ? (
                    <span style={{ color: 'red' }}>超时{result}</span>
                  ) : (
                    <>{record.leave_time ? <span className="luSe">已离场</span> : <a>待离场</a>}</>
                  )}
                </>
              )}
            </div>
          );
        },
      },
      {
        title: '创建时间',
        dataIndex: 'created_at',
      },
      // {
      //   title: '预定日期',
      //   dataIndex: '',
      // },

      {
        title: '操作',
        dataIndex: '',
        fixed: 'right',
        width: 100,
        render: (text, record) => {
          return (
            <>
              <Link to={`OrderDetails?id=${record.id}&xq=1`}>详情</Link>
            </>
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
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>兑换订单</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  {/* <Button type="primary" onClick={() => this.showModal(selectedRowKeys)}>
                    退款
                  </Button> */}
                  <Dropdown
                    overlay={menu}
                    placement="bottomLeft"
                    arrow={{ pointAtCenter: true }}
                    className="mL15"
                  >
                    <Button>导出</Button>
                  </Dropdown>
                </div>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              rowSelection={rowSelection}
              rowKey="id"
              columns={columns}
              dataSource={this.state.list}
              scroll={{ x: 1500 }}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: pageNum,
              }}
              // scroll={{ x: '100%' }}
            />
            <Modal
              title="退款"
              visible={this.state.newVenues}
              onOk={this.handleOk}
              onCancel={this.handleCancel}
            >
              <Form ref={this.formRefs}>
                <Form.Item
                  label="退款原因"
                  name="reason"
                  rules={[{ required: true, message: '请输入!' }]}
                >
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
