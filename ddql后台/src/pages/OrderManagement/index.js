import React from 'react';
import { UploadOutlined, LockOutlined } from '@ant-design/icons';
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
  Upload,
  Alert,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import ExportJsonExcel from 'js-export-excel';
import moment from 'moment';
import { method } from 'lodash';
import { getOrderStatusInfo } from '@/utils/statusConfig';
// import NewVenues from './components/NewVenues';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;
const layouts = {
  labelCol: { span: 5 },
  wrapperCol: { span: 16 },
};
class Login extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  formRefz = React.createRef();
  state = {
    spinning: false,
    pageNum: Number(this.props.location.query.pageNum ? this.props.location.query.pageNum : 1),
    list: [],
    statistical: { totalStatData: {}, todayStatData: {} },
    xxx: false,
    selectedRecord: [],
    aa: [],
    www: [],
    userList: [],
    shopList: [],
    barberList: [],
    selectedRowKeys: [],
    // 总订单数
    completedCount: 0,
    // 总收入
    totalIncome: 0,
  };

  componentDidMount() {
    this.getData();
  }

  // 获取发型师列表
  getBarberList = (shopId) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchField1: shopId,
      },
      url: '/ddql/shop/barber/lists',
      method: 'POST',
      myData: (res) => {
        if (res && res.code == 10000) {
          this.setState({
            barberList: res.data.list,
          });
        }
      },
    });
  };

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
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
            if (res && res.code === 10000) {
              console.log(res.data);
              this.setState({
                statistical: res.data,
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
    // console.log('selectedRowKeys changed: ', selectedRowKeys);
    this.setState({ selectedRowKeys });
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

  onFinish = (e) => {
    this.setState(
      {
        isSearch: true,
        startTime: e.time ? e.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        endTime: e.time ? e.time[1].format('YYYY-MM-DD 00:00:00') : undefined,
        pageNum: 1,
        searchStrField1: e.searchStrField1,
        searchStrField2: e.searchStrField2,
        searchIntStatus: e.searchIntStatus,
        keyword: e.keyword,
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
        isSearch: false,
        searchStrField1: undefined,
        searchStrField2: undefined,
        searchIntStatus: undefined,
        keyword: undefined,
        startTime: undefined,
        endTime: undefined,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  handleChange = (shopId) => {
    // 清除表单中发型师的值
    this.formRef.current.setFieldsValue({
      barberId: undefined,
    });
    this.getBarberList(shopId);
  };

  onChange = (value, dateString) => {
    console.log(dateString);
    this.setState({
      startTime: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
      endTime: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
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
  };

  Select = () => {
    if (this.state.selectedRecord.length == 0) {
      message.error('请选择需要操作的订单');
      return;
    }
  };

  downLoadOrderno = () => {
    let xhr = new XMLHttpRequest();
    let fileName = `待发货订单.xls`; // 文件名称
    const that = this;
    xhr.open('POST', `/ddql/order/exchange/un_dispatched/export`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.responseType = 'blob';
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
              a.href = objectUrl;
              a.download = fileName;
              document.body.appendChild(a);
              a.click();
              a.remove();
              that.setState({
                loading: false,
              });
            }
          } else {
            window.location = objectUrl;
          }
        }
      }
    };
    var data = {};
    xhr.send(JSON.stringify(data)); // 发送请求体（如果有）
  };

  export = () => {
    let xhr = new XMLHttpRequest();
    let fileName = `兑换订单.xls`; // 文件名称
    const that = this;
    xhr.open('POST', `/ddql/order/exchange/export`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.responseType = 'blob';
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
              a.href = objectUrl;
              a.download = fileName;
              document.body.appendChild(a);
              a.click();
              a.remove();
              that.setState({
                loading: false,
              });
            }
          } else {
            window.location = objectUrl;
          }
        }
      }
    };
    var data = {
      searchStrField1: this.state.searchStrField1,
      searchStrField2: this.state.searchStrField2,
      searchIntStatus: this.state.searchIntStatus,
      keyword: this.state.keyword,
      startTime: this.state.startTime,
      endTime: this.state.endTime,
    };
    xhr.send(JSON.stringify(data)); // 发送请求体（如果有）
  };

  handleOk = () => {
    this.formRefz.current.validateFields().then((values) => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          orderId: this.state.id,
          refundAmount: values.refundAmount,
          refundReason: values.refundReason,
        },
        method: 'POST',
        url: `/ddql/order/exchange/refund`,
        myData: (res) => {
          if (res.code === 10000) {
            message.success(res.msg);
            this.setState({
              refundVisible: false,
            });
            this.getData();
          } else {
            message.error(res.msg);
          }
        },
      });
    });
  };

  render() {
    const {
      selectedRowKeys,
      total,
      pageNum,
      xxx,
      shopList = [],
      barberList = [],
      completedCount,
      totalIncome,
      userList = [],
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
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
        width: 60,
      },
      {
        title: '订单号',
        dataIndex: 'orderNo',
      },
      {
        title: '商品',
        dataIndex: 'productName',
      },
      {
        title: '总数量',
        dataIndex: 'num',
        width: 80,
      },
      {
        title: '下单人',
        dataIndex: 'phone',
        render: (res, record) => (
          <div>
            {record.nickName}（{record.phone}）
          </div>
        ),
      },
      {
        title: '订单金额',
        dataIndex: 'amount',
        render: (res) => <div>{res}金币</div>,
      },
      {
        title: '快递单号',
        dataIndex: 'expressNo',
      },
      {
        title: '备注',
        dataIndex: 'remark',
        width: 90,
      },
      {
        title: '下单时间',
        dataIndex: 'createTime',
      },
      {
        title: '订单状态',
        fixed: 'right',
        width: 90,
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
        title: '到货状态',
        fixed: 'right',
        dataIndex: 'expressStatus',
        render: (text, record) => {
          return (
            <div>
              {text == -2
                ? '无'
                : text == -1
                ? '待发货'
                : text == 0
                ? '在途'
                : text == 1
                ? '揽件'
                : text == 2
                ? '疑难'
                : text == 3
                ? '签收'
                : text == 4
                ? '退签'
                : text == 5
                ? '派件'
                : text == 6
                ? '退回'
                : text == 10
                ? '待清关'
                : text == 11
                ? '清关中'
                : text == 12
                ? '已清关'
                : text == 13
                ? '清关异常'
                : text == 14
                ? '收件人拒签'
                : ''}
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
              {record.status == 1 && (
                <span
                  className="dangerFont"
                  style={{ color: '#ff4d4f', cursor: 'pointer', paddingLeft: 10 }}
                  onClick={() =>
                    this.setState(
                      { refundVisible: true, id: record.id, amount: record.amount },
                      () => {
                        setTimeout((_) => {
                          this.formRefz.current.setFieldsValue({
                            refundAmount: record.amount,
                          });
                        }, 300);
                      },
                    )
                  }
                >
                  退款
                </span>
              )}
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
          <div style={{ backgroundColor: '#fff', padding: '20px', marginBottom: 15 }}>
            <Row justify="space-between" align="middle">
              <Col>
                {this.state.orderNo ||
                this.state.phone ||
                this.state.status ||
                this.state.shopId ||
                this.state.barberId ||
                this.state.startTime ||
                this.state.endTime ? (
                  <>
                    <h1 style={{ fontWeight: '600', fontSize: '18px', marginBottom: 5 }}>
                      筛选结果统计
                    </h1>
                    <span style={{ color: '#ccc' }}>基于筛选结果订单的数据统计</span>
                  </>
                ) : (
                  <>
                    <h1 style={{ fontWeight: '600', fontSize: '18px', marginBottom: 5 }}>
                     {this.state.isSearch?'筛选结果统计':'今日订单统计'}
                    </h1>
                    <span style={{ color: '#ccc' }}>基于今日全部订单的数据统计d</span>
                  </>
                )}
              </Col>
              <Col>
                <Row gutter={16}>
                  <Col style={{ textAlign: 'center', width: 150 }}>
                    <h1 style={{ marginBottom: 0, fontSize: 21 }}>
                      {this.state.statistical.todayStatData.orderNum || 0}
                    </h1>
                    <span style={{ color: '#ccc' }}>今日订单数</span>
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
                  <Col style={{ textAlign: 'center', width: 150 }}>
                    <h1 style={{ marginBottom: 0, fontSize: 21 }}>
                      {this.state.statistical.todayStatData.totalAmount || 0} 金币
                    </h1>
                    <span style={{ color: '#ccc' }}>今日金币兑换总数</span>
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
                  <Col style={{ textAlign: 'center', width: 150 }}>
                    <h1 style={{ marginBottom: 0, fontSize: 21 }}>
                      {this.state.statistical.totalStatData.totalAmount || 0} 金币
                    </h1>
                    <span style={{ color: '#ccc' }}>累计金币兑换总数</span>
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
                  <Col style={{ textAlign: 'center', width: 150 }}>
                    <h1 style={{ marginBottom: 0, fontSize: 21 }}>
                      {this.state.statistical.totalStatData.orderNum || 0}
                    </h1>
                    <span style={{ color: '#ccc' }}>累计订单数</span>
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
                </Row>
              </Col>
            </Row>
          </div>

          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
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
                <Col className="gutter-row" span={5}>
                  <Form.Item label="下单人" name="keyword">
                    <Input placeholder="请输入下单人姓名、手机号" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={5}>
                  <Form.Item label="订单状态" name="searchIntStatus">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>已完成</Option>
                      <Option value={2}>退款中</Option>
                      <Option value={3}>已退款</Option>
                    </Select>
                  </Form.Item>
                </Col>

                {xxx && (
                  <>
                    <Col className="gutter-row" span={5}>
                      <Form.Item label="下单时间" name="time">
                        <RangePicker format="YYYY-MM-DD" />
                      </Form.Item>
                    </Col>
                  </>
                )}

                <Col className="gutter-row" style={{ textAlign: 'right', flex: '1 0 220px' }}>
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>

                    <Button className="mL15" onClick={this.resets}>
                      重置
                    </Button>
                    {xxx == false ? (
                      <a style={{ paddingLeft: 10 }} onClick={() => this.aperto(true)}>
                        展开 <DownOutlined />
                      </a>
                    ) : (
                      <a style={{ paddingLeft: 10 }} onClick={() => this.aperto(false)}>
                        收起 <UpOutlined />
                      </a>
                    )}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </div>
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>兑换订单</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button
                    type="primary"
                    onClick={() =>
                      this.setState({
                        tickVisible: true,
                      })
                    }
                  >
                    发货
                  </Button>
                  {/* <Button
                    danger
                    className="mL15"
                    onClick={() =>
                      this.setState({ refundVisible: true }, () => {
                        setTimeout((_) => {
                          this.formRefz.current.setFieldsValue({
                            refundAmount: record.amount,
                          });
                        }, 300);
                      })
                    }
                  >
                    退款
                  </Button> */}
                  <Button onClick={this.export} className="mL15">
                    导出
                  </Button>
                </div>
              </Col>
            </Row>

            <Table
              className="csdivcenter"
              style={{ marginTop: 15 }}
              rowSelection={rowSelection}
              rowKey="orderNo"
              columns={columns}
              dataSource={this.state.list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: pageNum,
              }}
              scroll={{ x: 1500 }}
            />
            <Modal
              title="退款"
              open={this.state.refundVisible}
              onOk={this.handleOk}
              onCancel={() => this.setState({ refundVisible: false })}
            >
              <Form ref={this.formRefz}>
                <Form.Item
                  label={
                    <span>
                      <span style={{ color: 'red' }}>*</span>退款币额
                    </span>
                  }
                >
                  <Form.Item
                    name="refundAmount"
                    noStyle
                    rules={[{ required: true, message: '请输入退款金额!' }]}
                  >
                    <InputNumber min={0} />
                  </Form.Item>
                  <div style={{ color: '#ccc' }}>订单总币额：{this.state.amount}金币</div>
                </Form.Item>

                <Form.Item
                  label="退款原因"
                  name="refundReason"
                  rules={[{ required: true, message: '请输入退款原因!' }]}
                >
                  <TextArea rows={4} placeholder="请输入" />
                </Form.Item>
              </Form>
            </Modal>
            <Modal
              title={`批量导入发货快递号`}
              open={this.state.tickVisible}
              onOk={(e) => this.setState({ tickVisible: false })}
              onCancel={(e) => this.setState({ tickVisible: false })}
            >
              <Form {...layouts}>
                <Alert
                  type="info"
                  showIcon
                  style={{ marginBottom: 20 }}
                  message={
                    <div>
                      批量导入请<a onClick={this.downLoadOrderno}>点击下载待发货订单</a>
                      ，填写后上传文件批量导入。
                    </div>
                  }
                />

                <Form.Item
                  name="files"
                  label="导入文件"
                  rules={[
                    {
                      required: true,
                      message: '请上传',
                    },
                  ]}
                >
                  <Upload
                    action="/ddql/order/exchange/express_no/import"
                    onChange={(e) => {
                      if (e.file.status == 'done') {
                        const { response } = e.file;
                        if (response.code == 10000) {
                          message.success(response.msg);
                          this.setState({ tickVisible: false }, () => {
                            this.getData();
                          });
                        } else {
                          message.error(response.msg);
                        }
                      }
                    }}
                    headers={{ token: localStorage.getItem('token') }}
                  >
                    <Button icon={<UploadOutlined />}>上传文件</Button>
                  </Upload>
                </Form.Item>
                <div
                  style={{
                    position: 'relative',
                    color: 'rgba(0, 0, 0, 0.427450980392157)',
                    left: 120,
                  }}
                >
                  <span>支持扩展名：.xls .xlsx</span>
                </div>
              </Form>
            </Modal>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(Login);
