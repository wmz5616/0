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
  Modal,
  DatePicker,
  Divider,
  InputNumber,
  Radio,
  Image,
  Alert,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { getOrderStatusInfo } from '@/utils/statusConfig';
// import AddAdministrator from './components/AddAdministrator';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { TextArea } = Input;
const { RangePicker } = DatePicker;
//兑换订单
//兑换订单
//兑换订单
class Login extends React.Component {
  formRefz = React.createRef();
  state = {
    spinning: false,
    orderNo: undefined,
    // 订单详情
    orderDetail: {},
    // 关联发型师详情
    orderShopBarber: {},
    // 订单服务列表
    orderOrgServes: [],
    // 订单日志
    orderLogs: [],
  };

  componentDidMount() {
    const { id } = this.props.location.query;
    if (id) {
      this.setState(
        {
          id,
        },
        () => {
          this.getData();
        },
      );
    }
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        //基本信息
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchId: this.state.id,
          },
          url: `/ddql/order/exchange/info`,
          method: 'POST',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 10000) {
              const order = res.data.orderInfo;
              this.setState({
                orderDetail: order,
                orderOrgServes: [res.data.orderInfo],
                orderLogs: res.data.logList,
                orderShopBarber: res.data.orderShopBarber,
              });
            } else {
              message.error(res.msg);
            }
          },
        });
      },
    );
  };

  showModal = () => {
    this.setState({
      isModalVisible: true,
    });
  };

  handleOk = () => {
    this.formRefz.current.validateFields().then((values) => {
      console.log(values);
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          orderNo: this.state.orderNo,
          // 订单金额 ，单位为分
          orderPrice: this.state.orderDetail.orderPrice * 100,
          // 退款类型
          refundType: 1,
          // 后台退款
          handleType: 3,
          // 理发订单
          orderType: 1,
          refundReason: values.refundReason,
          // 退款金额 ，单位为分
          refundPrice: values.refundPrice * 100,
        },
        method: 'POST',
        url: `/ddql/refund/apply`,
        myData: (res) => {
          if (res.code === 10000) {
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
    this.formRefz.current.resetFields();
    this.setState({
      isModalVisible: false,
      NewRoles: false,
    });
  };

  render() {
    const {
      selectedRowKeys,
      isModalVisible,
      orderDetail = {},
      orderShopBarber = {},
      statusInfo = {},
      orderOrgServes = [],
      orderLogs = [],
    } = this.state;

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '商品编号',
        dataIndex: 'productNo',
      },
      {
        title: '商品名称',
        dataIndex: 'productName',
      },
      {
        title: '图片',
        dataIndex: 'coverImage',
        render: (text, record) => {
          return (
            <>
              <img src={text} alt="" style={{ width: 103.5, height: 37.5, objectFit: 'contain' }} />
            </>
          );
        },
      },
      {
        title: '商品规格',
        dataIndex: 'specification',
      },
      {
        title: '单位',
        dataIndex: 'unit',
      },
      {
        title: '数量',
        dataIndex: 'num',
      },
      {
        title: '订单金额',
        dataIndex: 'amount',
        render: (res) => <div>{res}金币</div>,
      },
    ];

    //操作记录
    const columnsss = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '操作时间',
        dataIndex: 'createTime',
      },
      {
        title: '操作',
        dataIndex: 'handle',
      },

      {
        title: '详情',
        dataIndex: 'details',
      },
    ];

    const routes = [
      {
        // path:`VenueDetails?id=${id}` ,
        breadcrumbName: '首页',
      },
      {
        // path:`VenueDetails?id=${id}` ,
        breadcrumbName: '订单管理',
      },
      {
        // path: `/VenueDetails?id=${id}`,
        breadcrumbName: '订单详情',
      },
    ];

    return (
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
          <div style={{ backgroundColor: '#fff', padding: 30 }}>
            <Row gutter={16} style={{ lineHeight: '32px' }}>
              <Col className="gutter-row" span={24}>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <h3>兑换订单基本信息</h3>
                  <Button onClick={() => window.history.back()}>返回</Button>
                </div>
                <Row gutter={16}>
                  <Col className="gutter-row" span={6}>
                    <div>订单ID：{orderDetail.id}</div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>订单号：{orderDetail.orderNo}</div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>
                      订单状态：
                      <span
                        style={{
                          color:
                            orderDetail.status == 1
                              ? '#11AF21'
                              : orderDetail.status == 2
                              ? 'rgba(245, 154, 35, 0.84)'
                              : 'rgba(217, 0, 27, 0.99)',
                        }}
                      >
                        {orderDetail.status == 1
                          ? '已完成'
                          : orderDetail.status == 2
                          ? '退款中'
                          : '已退款'}
                      </span>
                    </div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>到货状态：</div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>
                      下单人：
                      <span>
                        {orderDetail.nickName}（{orderDetail.phone}）
                      </span>
                    </div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>
                      订单金额：
                      <span>{orderDetail.amount}金币</span>
                    </div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>
                      下单时间：
                      <span>{orderDetail.createTime}</span>
                    </div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>
                      到期时间：
                      <span>{orderDetail.isVirtual == 1 ? orderDetail.createTime : ''}</span>
                    </div>
                  </Col>
                  <Col className="gutter-row" span={7}>
                    <div>备注：{orderDetail.remark}</div>
                  </Col>
                </Row>
              </Col>
            </Row>
            <Divider />

            <h3>订单详情</h3>
            {orderDetail.status == 4 && (
              <div style={{ textAlign: 'right', marginTop: '-37px' }}>
                <span className="huangse mL15">一个订单只能操作退款一次，请谨慎操作！</span>
                <Button type="primary" onClick={() => this.showModal()}>
                  退款
                </Button>
              </div>
            )}
            {/* <div>姓名(手机号)于2020-03-28 12:12:12操作退款，退款金额为100.00元，退款原因为这里显示退款原因</div> */}

            <Table
              style={{ marginTop: 25 }}
              columns={columns}
              dataSource={orderOrgServes}
              pagination={false}
            />

            <h3 style={{ marginTop: 30 }}>操作记录</h3>

            <Table
              style={{ marginTop: 25 }}
              rowKey="id"
              columns={columnsss}
              dataSource={orderLogs}
              pagination={false}
              scroll={{ y: 800 }}
            />

            <Modal
              title="退款"
              visible={isModalVisible}
              onOk={this.handleOk}
              onCancel={this.handleCancel}
            >
              <Form ref={this.formRefz}>
                <Form.Item
                  label={
                    <span>
                      <span style={{ color: 'red' }}>*</span>退款金额
                    </span>
                  }
                >
                  <Form.Item
                    name="refundPrice"
                    noStyle
                    rules={[{ required: true, message: '请输入退款金额!' }]}
                  >
                    <InputNumber
                      min={0}
                      defaultValue={orderDetail.totalPrice}
                      max={orderDetail.totalPrice}
                    />
                  </Form.Item>
                  <div style={{ color: '#ccc' }}>订单总金额(元)：{orderDetail.totalPrice}</div>
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
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(Login);
