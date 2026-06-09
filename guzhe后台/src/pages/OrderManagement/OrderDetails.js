import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import { history } from '@umijs/max';
import {
  Button,
  Col,
  Divider,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Row,
  Spin,
  Table,
} from 'antd';
import dayjs from 'dayjs';
import React from 'react';
const { TextArea } = Input;
const statusMap = {
  0: {
    text: '待支付',
    color: '#f79a71',
  },
  1: {
    text: '待使用',
    color: '#a400ff',
  },
  2: {
    text: '待发货',
    color: '#f79a71',
  },
  3: {
    text: '已发货',
    color: '#2e99ff',
  },
  4: {
    text: '已完成',
    color: '#27b43e',
  },
  5: {
    text: '退款中',
    color: '#8b8b8b',
  },
  6: {
    text: '已退款',
    color: '#da1c30',
  },
  7: {
    text: '已过期',
    color: '#8b8b8b',
  },
  8: {
    text: '已取消',
    color: '#8b8b8b',
  }
}
const lStatusMap = {
  [-2]: {
    text: '无',
    color: '#f79a71',
  },
  [-1]: {
    text: '待发货',
    color: '#f79a71',
  },
  0: {
    text: '在途',
    color: '#f79a71',
  },
  1: {
    text: '揽件',
    color: '#a400ff',
  },
  2: {
    text: '疑难',
    color: '#f79a71',
  },
  3: {
    text: '签收',
    color: '#2e99ff',
  },
  4: {
    text: '退签',
    color: '#27b43e',
  },
  5: {
    text: '派件',
    color: '#8b8b8b',
  },
  6: {
    text: '退回',
    color: '#da1c30',
  },
  10: {
    text: '待清关',
    color: '#8b8b8b',
  },
  11: {
    text: '清关中',
    color: '#8b8b8b',
  },
  12: {
    text: '已清关',
    color: '#8b8b8b',
  },
  13: {
    text: '清关异常',
    color: '#8b8b8b',
  },
  14: {
    text: '收件人拒签',
    color: '#8b8b8b',
  }
}

//兑换订单
class Login extends React.Component {
  formRefz = React.createRef();
  state = {
    spinning: false,
    orderNo: undefined,
    // 订单详情
    orderDetail: {},
    // 订单服务列表
    orderOrgServes: [],
    // 订单日志
    orderLogs: [],
  };

  componentDidMount() {
    const searchParams = new URLSearchParams(history.location?.search || '');
    const id = searchParams.get('id');
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
      async () => {
        //基本信息
        const res = await post(`/guzhe/product_order/info`, {
          searchId: this.state.id,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          const order = res.data.orderInfo;
          const ticket = res.data?.ticketList?.map((item) => item.ticket).join(',') || '';
          this.setState({
            orderDetail: order,
            orderOrgServes: order ? [{
              ...order,
              ticket: ticket
            }] : [],
            orderLogs: res.data.logList,
          });
        } else {
          message.error(res?.msg);
        }
      },
    );
  };

  showModal = () => {
    this.setState({
      isModalVisible: true,
    },()=>{
      this.formRefz.current.setFieldsValue({
        refundAmount: this.state.orderDetail.amount / 100,
        refundReason: '',
      })
    });
  };

  handleOk = () => {
    this.formRefz.current.validateFields().then(async (values) => {
      const res = await post(`/guzhe/product_order/refund`, {
        orderId: this.state.id,
        refundAmount: values.refundAmount * 100,
        refundReason: values.refundReason,
      });
      if (res.code == 10000) {
        message.success(res.msg);
        this.setState({
          isModalVisible: false,
        });
        this.getData();
      } else {
        message.error(res?.msg);
      }
    });
  };

  handleCancel = () => {
    this.formRefz.current.resetFields();
    this.setState({
      isModalVisible: false,
    });
  };

  render() {
    const {
      isModalVisible,
      orderDetail = {},
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
        title: '图片',
        dataIndex: 'coverImage',
        render: (text, record) => {
          return (
            <>
              <img
                src={text}
                alt=""
                style={{ width: 103.5, height: 37.5, objectFit: 'contain' }}
              />
            </>
          );
        },
      },
      {
        title: '商品名称',
        dataIndex: 'productName',
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
        render: (res) => <div>{res / 100 || 0}元</div>,
      },
      {
        title: '券码',
        dataIndex: 'ticket',
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
                <div
                  style={{ display: 'flex', justifyContent: 'space-between' }}
                >
                  <h3>商品订单基本信息</h3>
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
                            statusMap[orderDetail.status]?.color,
                        }}
                      >
                        {statusMap[orderDetail.status]?.text}
                      </span>
                    </div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>到货状态：
                      <span
                        style={{
                          color:
                            lStatusMap[orderDetail.expressStatus]?.color,
                        }}
                      >
                        {lStatusMap[orderDetail.expressStatus]?.text}
                      </span></div>
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
                      <span>{orderDetail.amount / 100 || 0}元</span>
                    </div>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <div>
                      下单时间：
                      <span>{dayjs(orderDetail.createTime).format('YYYY-MM-DD HH:mm:ss')}</span>
                    </div>
                  </Col>
                  {orderDetail.isVirtual == 1 && (
                    <Col className="gutter-row" span={6}>
                      <div>
                        到期时间：
                        <span>
                          {dayjs(orderDetail.deadline).format('YYYY-MM-DD HH:mm:ss')}
                        </span>
                      </div>
                    </Col>)}
                  <Col className="gutter-row" span={7}>
                    <div>商家：{orderDetail.shopName}</div>
                  </Col>
                </Row>
              </Col>
            </Row>
            <Divider />

            <h3>订单详情</h3>
            {/* 待使用，待发货，已发货，已完成，已过期才能退款 */}
            {[1,2,3,4,7].includes(orderDetail.status) ? (
              <div style={{ textAlign: 'right', marginTop: '-37px' }}>
                <span className="huangse mL15">
                  一个订单只能操作退款一次，请谨慎操作！
                </span>
                <Button danger onClick={() => this.showModal()}>
                  退款
                </Button>
              </div>
            ) : null}
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
                    name="refundAmount"
                    noStyle
                    rules={[{ required: true, message: '请输入退款金额!' }]}
                  >
                    <InputNumber
                      min={0}
                      max={orderDetail.amount / 100}
                    />
                  </Form.Item>
                  <div style={{ color: '#ccc' }}>
                    订单总金额(元)：{orderDetail.amount / 100 || 0}元
                  </div>
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

export default Login;
