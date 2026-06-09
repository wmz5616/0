import React from 'react';
import { QuestionCircleOutlined, LockOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Tabs,
  Select,
  DatePicker,
  Modal,
  Progress,
  Upload,
  Tooltip,
  Table,
  Radio,
  Tag,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
// import { Map, Marker } from 'react-amap';  // 若用不到地图相关，可先注释

// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { TabPane } = Tabs;
const { RangePicker } = DatePicker;
const { Column } = Table;

class UserDetails extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    roleData: [
      { roleId: 1, roleName: '哈哈' },
      { roleId: 2, roleName: '哈哈333' },
    ],
    info: {}, // 用户信息
    listData: [], // 消费记录表格数据
    selectedRowKeys: [], // 表格选中项
    info: {},
    pageNums: 1,
    pageNum: 1,
    list: [],
    radioValue: 1,
    selectedRowKeyss: [],
  };

  componentDidMount() {
    this.getInfo();
  }

  getwithdrawalData = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchField1: this.state.info.id,
        searchStrField1: this.state.searchStrField1,
        searchStrField2: this.state.searchStrField2,
        searchIntStatus: this.state.searchIntStatus,
        keyword: this.state.keyword,
        startTime: this.state.startTime,
        endTime: this.state.endTime,
        pageNum: this.state.pageNums,
      },
      url: `/ddql/order/withdrawal/lists`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
          this.setState({
            list: res.data.list,
            totals: res.data.total,
          });
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  getReacData = () => {
    const params = {
      searchField1: this.state.info.id,
      startTime: this.state.startTime,
      endTime: this.state.endTime,
      keyword: this.state.keyword,
      searchIntStatus: this.state.searchIntStatus,
      pageNum: this.state.pageNum,
    };
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: `/ddql/order/recharge/lists`,
      method: 'POST',
      myData: (res) => {
        console.log(res);
        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
          this.setState({
            listData: res.data.list,
            total: res.data.total,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  getData = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchField1: this.state.info.id,
        searchStrField1: this.state.searchStrField1,
        searchStrField2: this.state.searchStrField2,
        searchIntStatus: this.state.searchIntStatus,
        keyword: this.state.keyword,
        startTime: this.state.startTime,
        endTime: this.state.endTime,
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
            listData: res.data.list,
            total: res.data.total,
          });
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  getInfo = () => {
    const { query } = this.props.location;
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: query.id,
      },
      url: `/ddql/user/detail`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState(
            {
              info: res.data,
            },
            () => {
              this.getData();
              this.getwithdrawalData();
            },
          );
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  // 表格选中事件
  onSelectChange = (selectedRowKeys) => {
    this.setState({ selectedRowKeys });
  };

  onFinish = (e) => {
    this.setState(
      {
        pageNum: 1,
        searchStrField1: e.searchStrField1,
        searchStrField2: e.searchStrField2,
        searchIntStatus: e.searchIntStatus,
        keyword: e.keyword,
      },
      () => {
        if (this.state.radioValue == 2) {
          this.getReacData();
        } else {
          this.getData();
        }
      },
    );
  };

  preciseDivide = (a, b, factor = 100) => {
    return (a * factor) / (b * factor);
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        searchStrField1: undefined,
        searchStrField2: undefined,
        searchIntStatus: undefined,
        keyword: undefined,
        startTime: undefined,
        endTime: undefined,
        pageNum: 1,
      },
      () => {
        if (this.state.radioValue == 2) {
          this.getReacData();
        } else {
          this.getData();
        }
      },
    );
  };

  exports = () => {
    const that = this;
    let xhr = new XMLHttpRequest();
    let fileName = `健康币提现记录.xls`; // 文件名称
    const url = `/ddql/order/withdrawal/export`;
    xhr.open('POST', url, true);
    xhr.responseType = 'blob';
    xhr.setRequestHeader('Content-Type', 'application/json');
    const data = {
      searchField1: this.state.info.id,
      searchStrField1: this.state.searchStrField1,
      searchStrField2: this.state.searchStrField2,
      searchIntStatus: this.state.searchIntStatus,
      keyword: this.state.keyword,
      startTime: this.state.startTime,
      endTime: this.state.endTime,
      pageNum: this.state.pageNums,
    };

    xhr.setRequestHeader('token', localStorage.getItem('token')); // 请求头中的验证信息等（如果有）
    xhr.onload = function () {
      if (this.status === 200) {
        let type = xhr.getResponseHeader('Content-Type');
        let blob = new Blob([this.response], { type: type });
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(blob, fileName);
        } else {
          let URL = window.URL || window.webkitURL;
          let objectUrl = URL.createObjectURL(blob);
          if (fileName) {
            var a = document.createElement('a');
            // safari doesn't support this yet
            if (typeof a.download === 'undefined') {
              window.location = objectUrl;
            } else {
              console.log(objectUrl);
              a.href = objectUrl;
              a.download = fileName;
              document.body.appendChild(a);
              a.click();
              a.remove();
            }
          } else {
            window.location = objectUrl;
          }
        }
      }
    };
    xhr.send(JSON.stringify(data));
  };

  export = () => {
    const that = this;
    let xhr = new XMLHttpRequest();
    let fileName = this.state.radioValue == 1 ? `兑换商品列表.xls` : `充值记录列表.xls`; // 文件名称
    const url =
      this.state.radioValue == 1 ? `/ddql/order/exchange/export` : `/ddql/order/recharge/lists`;
    xhr.open('POST', url, true);
    xhr.responseType = 'blob';
    xhr.setRequestHeader('Content-Type', 'application/json');
    const data = {
      startTime: this.state.startTime,
      endTime: this.state.endTime,
      keyword: this.state.keyword,
      searchIntStatus: this.state.searchIntStatus,
      searchField1: this.state.info.id,
    };

    xhr.setRequestHeader('token', localStorage.getItem('token')); // 请求头中的验证信息等（如果有）
    xhr.onload = function () {
      if (this.status === 200) {
        let type = xhr.getResponseHeader('Content-Type');
        let blob = new Blob([this.response], { type: type });
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(blob, fileName);
        } else {
          let URL = window.URL || window.webkitURL;
          let objectUrl = URL.createObjectURL(blob);
          if (fileName) {
            var a = document.createElement('a');
            // safari doesn't support this yet
            if (typeof a.download === 'undefined') {
              window.location = objectUrl;
            } else {
              console.log(objectUrl);
              a.href = objectUrl;
              a.download = fileName;
              document.body.appendChild(a);
              a.click();
              a.remove();
            }
          } else {
            window.location = objectUrl;
          }
        }
      }
    };
    xhr.send(JSON.stringify(data));
  };

  render() {
    const { info = {} } = this.state;
    const { id } = this.props.location.query;
    const { 消费记录数据, selectedRowKeys } = this.state;
    const routes = [
      {
        path: `/`,
        breadcrumbName: '首页',
      },
      {
        breadcrumbName: '用户管理',
      },
      {
        breadcrumbName: '用户详情',
      },
    ];

    // 消费状态映射，结合实际需求调整
    const 消费状态映射 = {
      0: '待付款',
      1: '已完成',
      2: '退款中',
      3: '已退款',
      4: '已取消',
    };
    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
        width: 80,
      },
      {
        title: '订单号',
        dataIndex: 'orderNo',
      },
      {
        title: '商品内容',
        dataIndex: 'productName',
      },
      {
        title: '总数量',
        dataIndex: 'num',
        width: 80,
      },
      {
        title: '消费金额',
        dataIndex: 'amount',
        render: (res) => <div>{res}金币</div>,
      },
      {
        title: '备注',
        dataIndex: 'remark',
        width: 90,
      },
      {
        title: '消费时间',
        dataIndex: 'createTime',
      },
      {
        title: '订单状态',
        fixed: 'right',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div
              style={{
                color:
                  text == 1
                    ? '#11AF21'
                    : text == 2
                    ? 'rgba(245, 154, 35, 0.84)'
                    : 'rgba(217, 0, 27, 0.99)',
              }}
            >
              {text == 1 ? '已完成' : text == 2 ? '退款中' : '已退款'}
            </div>
          );
        },
      },
      {
        title: '操作',
        dataIndex: '',
        fixed: 'right',
        width: 100,
        render: (text, record) => {
          return (
            <div>
              <span
                style={{ color: '#1890ff', cursor: 'pointer' }}
                onClick={() => {
                  history.push(`/OrderManagement/OrderDetails?id=${record.id}`);
                }}
              >
                详情
              </span>
            </div>
          );
        },
      },
    ];
    const columnsd = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '交易编号',
        dataIndex: 'orderNo',
      },
      {
        title: '充值金额/元',
        dataIndex: 'amount',
        render: (res) => <div>{this.preciseDivide(res, 100)}</div>,
      },
      {
        title: '赠送金额/元',
        dataIndex: 'giveAmount',
        render: (res) => <div>{this.preciseDivide(res, 100)}</div>,
      },
      {
        title: '充值团体',
        dataIndex: 'teamName',
      },
      {
        title: '充值时间',
        dataIndex: 'createTime',
      },
      {
        title: '充值状态',
        // fixed: 'right',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              {record.status == 1 && <span className="huangse">待付款</span>}
              {record.status == 2 && <span className="luSe">已完成</span>}
              {record.status == 3 && <span style={{ color: '#ccc' }}>已取消</span>}
              {record.status == 4 && <span className="red">已退款</span>}
              {record.status == 4 && (
                <Tooltip title={record.refundRemark}>
                  <QuestionCircleOutlined
                    style={{ fontSize: 18, color: '#ccc', cursor: 'pointer', marginLeft: 4 }}
                  />
                </Tooltip>
              )}
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
              breadcrumb: {
                itemRender: this.itemRender,
                routes,
              },
            }}
          >
            <Spin spinning={this.state.spinning}>
              {/* 用户基本信息区域 */}
              <div style={{ backgroundColor: '#fff', marginBottom: 15, padding: 20 }}>
                <Row>
                  {/* 头像等（这里假设 info.pic 是头像） */}
                  <Col span={2}>
                    <img
                      src={info.avatar || ''}
                      alt="avatar"
                      style={{
                        width: '80px',
                        height: '80px',
                        objectFit: 'cover',
                        borderRadius: '4px',
                      }}
                    />
                  </Col>
                  <Col span={18}>
                    <div style={{ display: 'flex' }}>
                      <h2 style={{ marginBottom: 20, marginRight: 30 }}>
                        {info.nickname || 'Hong'}
                      </h2>
                      {/* <Button type="default" className="darkBlue-btn">
                        编辑
                      </Button> */}
                    </div>
                    <Row style={{ marginBottom: 8 }}>
                      <Col span={4}>
                        <span style={{ fontWeight: 'bold' }}>真实姓名：</span>
                        {info.name}
                      </Col>
                      <Col span={4}>
                        <span style={{ fontWeight: 'bold' }}>籍贯：</span>
                        {info.area}
                      </Col>
                      <Col span={4}>
                        <span style={{ fontWeight: 'bold' }}>生日：</span>
                        {info.birthDate}
                      </Col>
                      <Col span={4}>
                        <span style={{ fontWeight: 'bold' }}>联系电话：</span>
                        {info.phone}
                      </Col>
                      <Col span={4}>
                        <span style={{ fontWeight: 'bold' }}>身份证号码：</span>
                        {info.cardNum}
                      </Col>
                    </Row>
                    <Row style={{ marginBottom: 8 }}>
                      <Col span={8}>
                        <span style={{ fontWeight: 'bold' }}>注册时间：</span>
                        {info.createTime}
                      </Col>
                      <Col span={8}>
                        <span style={{ fontWeight: 'bold' }}>所属团体：</span>
                        {info.teamNames}
                      </Col>
                    </Row>
                  </Col>
                  <Col span={4}>
                    <Row gutter={18} justify="end">
                      <Col style={{ textAlign: 'center' }}>
                        <span style={{ color: '#ccc' }}>健康币数</span>
                        <h1 style={{ marginBottom: 0, color: '#1890FF' }}>{info.healthCoin}</h1>
                        <div
                          style={{
                            borderRight: '1px solid #e9e9e9',
                            height: 35,
                            float: 'right',
                            position: 'absolute',
                            top: 12,
                            right: 0,
                          }}
                        />
                      </Col>
                      <Col style={{ textAlign: 'center' }}>
                        <span style={{ color: '#ccc' }}>金币数</span>
                        <h1 style={{ marginBottom: 0, color: '#1890FF' }}>{info.goldCoin}</h1>
                        <div
                          style={{
                            borderRight: '1px solid #e9e9e9',
                            height: 35,
                            float: 'right',
                            position: 'absolute',
                            top: 12,
                            right: 0,
                          }}
                        />
                      </Col>
                      <Col style={{ textAlign: 'center', width: 100 }}>
                        <span style={{ color: '#ccc' }}>用户状态</span>
                        <h1 style={{ marginBottom: 0, color: '#1890FF' }}>
                          {info.lock == 0 ? '正常' : '异常'}
                        </h1>
                      </Col>
                    </Row>
                    <Button
                      style={{ float: 'right', marginTop: 20 }}
                      onClick={() => history.goBack()}
                    >
                      返回
                    </Button>
                  </Col>
                </Row>
              </div>
              {/* 筛选查询区域 */}
              <div
                style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}
              >
                <Form ref={this.formRef} onFinish={this.onFinish}>
                  <Row gutter={16}>
                    <Col className="gutter-row" span={5}>
                      <Form.Item label="订单号" name="searchStrField1">
                        <Input placeholder="请输入" />
                      </Form.Item>
                    </Col>
                    <Col className="gutter-row" span={5}>
                      <Form.Item label="商品名称" name="searchStrField2">
                        <Input placeholder="请输入" />
                      </Form.Item>
                    </Col>
                    {/* <Col className="gutter-row" span={5}>
                      <Form.Item label="下单人" name="keyword">
                        <Input placeholder="请输入下单人姓名、手机号" />
                      </Form.Item>
                    </Col> */}

                    <Col className="gutter-row" span={5}>
                      <Form.Item label="订单状态" name="searchIntStatus">
                        {this.state.radioValue == 1 ? (
                          <Select allowClear placeholder="请选择">
                            <Option value={1}>已完成</Option>
                            <Option value={2}>退款中</Option>
                            <Option value={3}>已退款</Option>
                          </Select>
                        ) : (
                          <Select placeholder="请选择">
                            <Option value={1}>待付款</Option>
                            <Option value={2}>已完成</Option>
                            <Option value={3}>已取消</Option>
                            <Option value={4}>已退款</Option>
                          </Select>
                        )}
                      </Form.Item>
                    </Col>
                    <Form.Item style={{ position: 'absolute', right: 20 }}>
                      <Button type="primary" htmlType="submit">
                        查询
                      </Button>

                      <Button className="mL15" onClick={this.resets}>
                        重置
                      </Button>
                    </Form.Item>
                  </Row>
                </Form>
              </div>

              {/* 消费记录表格区域 */}
              <div style={{ backgroundColor: '#fff', padding: 20 }}>
                <Row style={{ marginBottom: 10, justifyContent: 'space-between' }}>
                  <Col span={6}>
                    {' '}
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <h1 style={{ fontWeight: '600', fontSize: '18px' }}>消费记录</h1>
                      <Radio.Group
                        onChange={(e) =>
                          this.setState(
                            {
                              radioValue: e.target.value,
                              pageNum: 1,
                            },
                            () => {
                              if (e.target.value == 2) {
                                this.getReacData();
                              } else {
                                this.getData();
                              }
                            },
                          )
                        }
                        value={this.state.radioValue}
                        buttonStyle="solid"
                        style={{ marginLeft: 15 }}
                      >
                        <Radio.Button value={1}>兑换商品</Radio.Button>
                        <Radio.Button value={2}>购买健康币</Radio.Button>
                      </Radio.Group>
                    </div>
                  </Col>
                  <Col>
                    <Button onClick={this.export}>导出</Button>
                  </Col>
                </Row>
                <Table
                  rowKey="id"
                  dataSource={this.state.listData}
                  columns={this.state.radioValue == 1 ? columns : columnsd}
                  rowSelection={{
                    selectedRowKeys,
                    onChange: this.onSelectChange,
                  }}
                  pagination={{
                    total: this.state.total, // 假设总条数，实际结合接口
                    pageSize: 10,
                    current: this.state.pageNum,
                    onChange: (page) => {
                      this.setState(
                        {
                          pageNum: page,
                        },
                        () => {
                          if (this.state.radioValue == 2) {
                            this.getReacData();
                          } else {
                            this.getData();
                          }
                        },
                      );
                    },
                  }}
                />
              </div>
              <div style={{ backgroundColor: '#fff', padding: 20 }}>
                <Row style={{ marginBottom: 10, justifyContent: 'space-between' }}>
                  <Col span={6}>
                    {' '}
                    <h1 style={{ fontWeight: '600', fontSize: '18px' }}>健康币提现记录</h1>
                  </Col>
                  <Col>
                    <Button onClick={this.exports}>导出</Button>
                  </Col>
                </Row>
                <Table
                  rowKey="id"
                  dataSource={this.state.list}
                  columns={[
                    {
                      title: '序号',
                      dataIndex: 'index',
                      render: (text, record, index) => index + 1,
                    },
                    {
                      title: '订单编号',
                      dataIndex: 'outBillNo',
                    },
                    {
                      title: '健康币来源',
                      dataIndex: 'teamName',
                    },
                    {
                      title: '交易健康币数',
                      dataIndex: 'amount',
                    },
                    {
                      title: '交易金额（元）',
                      dataIndex: 'amount',
                    },
                    {
                      title: '消费时间',
                      dataIndex: 'createTime',
                    },
                  ]}
                  rowSelection={{
                    selectedRowKeys: this.state.selectedRowKeyss,
                    onChange: (e) => this.setState({ selectedRowKeyss: e }),
                  }}
                  pagination={{
                    total: this.state.totals, // 假设总条数，实际结合接口
                    current: this.state.pageNums,
                    pageSize: 10,
                    onChange: (page) => {
                      this.setState(
                        {
                          pageNums: page,
                        },
                        () => {
                          this.getwithdrawalData();
                        },
                      );
                    },
                  }}
                />
              </div>
            </Spin>
          </PageContainer>
        </div>
      </div>
    );
  }

  // 根据消费状态获取标签颜色
  getStatusColor = (status) => {
    switch (status) {
      case '0':
        return 'orange';
      case '1':
        return 'green';
      case '2':
        return 'red';
      case '3':
        return 'gray';
      case '4':
        return 'default';
      default:
        return 'default';
    }
  };
}

export default connect()(UserDetails);
