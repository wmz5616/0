import { post } from '@/utils/request';
import { UploadOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { history } from '@umijs/max';
import {
  Alert,
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Row,
  Select,
  Spin,
  Table,
  Upload,
} from 'antd';
import dayjs from 'dayjs';
import React from 'react';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;
const layouts = {
  labelCol: { span: 5 },
  wrapperCol: { span: 16 },
};
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
  },
};

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
  },
};

class orderManage extends React.Component {
  formRef = React.createRef();
  formRefz = React.createRef();
  state = {
    spinning: false,
    list: [],
    selectedRowKeys: [],
    refundVisible: false,
    tickVisible: false,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    shopList: [],
  };

  componentDidMount() {
    this.getData();
    this.getShopList();
  }

  getShopList = async () => {
    const res = await post(`/guzhe/common/shop/lists`, {
      psearchIntStatus: 1,
    });
    if (res && res.code == 10000) {
      this.setState({
        shopList: res.data || [],
      });
    } else {
      message.error(res?.msg);
    }
  };

  getData = async () => {
    this.setState({ spinning: true });

    const params = {
      keyword: this.state.keyword,
      searchStrField2: this.state.productName,
      searchStrField3: this.state.deviceNo,
      searchStatusList: this.state.status,
      searchField4: this.state.shopId,
      startTime: this.state.startTime,
      endTime: this.state.endTime,
      searchStrField1: this.state.orderNo,
    };

    const [res, statData] = await Promise.all([
      post(`/guzhe/product_order/lists`, {
        ...params,
        pageNum: this.state.pageNum,
        pageSize: this.state.pageSize,
      }),
      post(`/guzhe/product_order/stat`, params),
    ]);

    this.setState({ spinning: false });

    // 处理列表数据
    if (res?.code === 10000) {
      this.setState({
        list: res.data.list,
        total: res.data.total,
      });
    } else {
      message.error(res?.msg || '获取订单列表失败');
    }

    // 处理统计数据
    if (statData?.code === 10000) {
      this.setState({
        statData: statData.data,
      });
    } else {
      message.error(statData?.msg || '获取统计数据失败');
    }
  };

  handleOk = () => {
    this.formRefz.current.validateFields().then(async (values) => {
      const res = await post(`/guzhe/product_order/refund`, {
        orderId: this.state.selectedItem.id,
        refundAmount: values.refundAmount * 100,
        refundReason: values.refundReason,
      });
      if (res.code == 10000) {
        message.success(res.msg);
        this.formRefz.current.resetFields();
        this.setState({
          refundVisible: false,
          selectedItem: {},
          selectedRowKeys: [],
        });
        this.getData();
      } else {
        message.error(res?.msg);
      }
    });
  };

  pageChange = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNum: page,
      },
      () => {
        this.getData();
      },
    );
  };

  onFinish = (vas) => {
    this.setState(
      {
        keyword: vas.keyword,
        startTime: vas.time
          ? vas.time[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        endTime: vas.time
          ? vas.time[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
        orderNo: vas.orderNo,
        productName: vas.productName,
        deviceNo: vas.deviceNo,
        shopId: vas.shopId,
        status: vas.status,
        pageNum: 1,
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
        keyword: undefined,
        startTime: undefined,
        endTime: undefined,
        orderNo: undefined,
        productName: undefined,
        deviceNo: undefined,
        shopId: undefined,
        status: undefined,
        selectedRowKeys: [],
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };
  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  exportList = () => {
    this.Export('/guzhe/product_order/export', '商品订单', {
      keyword: this.state.keyword,
      searchStrField2: this.state.productName,
      searchStrField3: this.state.deviceNo,
      searchStatusList: this.state.status,
      searchField4: this.state.shopId,
      startTime: this.state.startTime,
      endTime: this.state.endTime,
      searchStrField1: this.state.orderNo,
    });
  };

  exportModule = () => {
    this.Export('/guzhe/product_order/un_dispatched/export', '商品订单', {
      keyword: this.state.keyword,
      searchStrField2: this.state.productName,
      searchStrField3: this.state.deviceNo,
      searchField3: -1,
      searchStatusList: this.state.status,
      searchField4: this.state.shopId,
      startTime: this.state.startTime,
      endTime: this.state.endTime,
      searchStrField1: this.state.orderNo,
    });
  };

  Export = (url, name, params) => {
    let xhr = new XMLHttpRequest();
    let fileName = `${name}${dayjs().format('YYYY-MM-DD HH:mm:ss')}.xls`; // 文件名称
    const that = this;
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.responseType = 'blob';
    xhr.setRequestHeader('token', localStorage.getItem('token')); // 请求头中的验证信息等（如果有）

    xhr.onload = function () {
      if (this.status == 200) {
        let type = xhr.getResponseHeader('Content-Type');
        if (type.indexOf('application/json') != -1) {
          let reader = new FileReader();
          reader.onload = function () {
            let res = JSON.parse(this.result);
            message.error(res?.msg || '导出失败');
          };
          reader.readAsText(xhr.response);
          return;
        }

        let blob = new Blob([this.response], { type: type });
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(blob, fileName);
        } else {
          let URL = window.URL || window.webkitURL;
          let objectUrl = URL.createObjectURL(blob);
          if (fileName) {
            var a = document.createElement('a');
            // safari doesn't support this yet
            if (typeof a.download == 'undefined') {
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
          URL.revokeObjectURL(objectUrl);
        }
      }
    };
    xhr.send(JSON.stringify(params));
  };

  render() {
    const { selectedRowKeys, total } = this.state;
    const rowSelection = {
      selectedRowKeys,
      fixed: true,
      onChange: this.onSelectChange,
    };

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
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
      },
      {
        title: '商家',
        dataIndex: 'shopName',
      },
      {
        title: '设备编号',
        dataIndex: 'serialNumber',
      },
      {
        title: '下单人',
        dataIndex: 'nickName',
      },
      {
        title: '订单金额',
        dataIndex: 'amount',
        render: (text) => <span>{text / 100 || 0} 元</span>,
      },
      {
        title: '备注',
        dataIndex: 'remark',
      },
      {
        title: '下单时间',
        dataIndex: 'createTime',
      },
      {
        title: '订单状态',
        fixed: 'right',
        dataIndex: 'status',
        render: (text) => (
          <div
            style={{
              color: statusMap[text]?.color,
            }}
          >
            {statusMap[text]?.text}
          </div>
        ),
      },
      {
        title: '物流状态',
        fixed: 'right',
        dataIndex: 'expressStatus',
        render: (text) => (
          <div
            style={{
              color: lStatusMap[text]?.color,
            }}
          >
            {lStatusMap[text]?.text}
          </div>
        ),
      },
      {
        title: '操作',
        fixed: 'right',
        dataIndex: '',
        render: (text, record) => {
          return (
            <>
              <span
                className="clickFont"
                style={{ marginLeft: 10 }}
                onClick={() =>
                  history.push(`/OrderManagement/OrderDetails?id=${record.id}`)
                }
              >
                详情
              </span>
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
          <div
            style={{
              backgroundColor: '#fff',
              padding: '20px 20px 0 20px',
              marginBottom: 15,
            }}
          >
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="订单号" name="orderNo">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="商品名称" name="productName">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="设备编号" name="deviceNo">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="商家" name="shopId">
                    <Select
                      allowClear
                      placeholder="请选择"
                      showSearch
                      optionFilterProp="children"
                    >
                      {this.state.shopList.map((res) => (
                        <Option value={res.id}>{res.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="下单人" name="keyword">
                    <Input placeholder="请输入姓名或手机号" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="订单状态" name="status">
                    <Select mode="multiple" allowClear placeholder="请选择">
                      <Option value={0}>待支付</Option>
                      <Option value={1}>待使用</Option>
                      <Option value={2}>待发货</Option>
                      <Option value={3}>已发货</Option>
                      <Option value={4}>已完成</Option>
                      <Option value={5}>退款中</Option>
                      <Option value={6}>已退款</Option>
                      <Option value={7}>已过期</Option>
                      <Option value={8}>已取消</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="下单时间" name="time">
                    <RangePicker />
                  </Form.Item>
                </Col>
                <Col
                  className="gutter-row"
                  style={{ textAlign: 'right', flex: '1 0 220px' }}
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
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row gutter={16}>
              <Col span={6}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  商品订单
                </h1>
              </Col>
              <Col
                span={18}
                style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}
              >
                <Button
                  type="primary"
                  onClick={() => {
                    this.setState({ tickVisible: true });
                  }}
                >
                  发货
                </Button>
                <Button
                  variant="outlined"
                  color="red"
                  onClick={() => {
                    if (this.state.selectedRowKeys.length == 0) {
                      message.info('请选择订单');
                      return;
                    }
                    if (this.state.selectedRowKeys.length > 1) {
                      message.info('只能选择一个订单');
                      return;
                    }
                    const selectedItem = this.state.list.find(
                      (item) => item.id == this.state.selectedRowKeys[0],
                    );
                    // 待使用，待发货，已发货，已完成，已过期才能退款
                    if (![1, 2, 3, 4, 7].some((status) => status == selectedItem.status)) {
                      message.info('该订单状态不可操作退款');
                      return;
                    }
                    this.setState({
                      selectedItem,
                      refundVisible: true,
                    }, () => {
                      this.formRefz.current.setFieldsValue({
                        refundAmount: selectedItem.amount / 100,
                        refundReason: '',
                      })
                    });
                  }}
                >
                  退款
                </Button>
                <Button variant="outlined" onClick={this.exportList}>
                  导出
                </Button>
              </Col>
              <Col span={24} style={{ margin: '10px 0' }}>
                <Alert
                  showIcon
                  message={`订单总数量：${this.state.statData?.orderNum || 0
                    } | 订单总金额：${this.state.statData?.totalAmount
                      ? (this.state.statData?.totalAmount / 100).toFixed(2)
                      : 0
                    } 元`}
                ></Alert>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              rowSelection={rowSelection}
              rowKey="id"
              columns={columns}
              dataSource={this.state.list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: this.state.pageSize,
                total,
                current: this.state.pageNum,
              }}
              scroll={{ x: 'max-content' }}
            />
          </div>
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
                    max={this.state.selectedItem?.amount / 100}
                  />
                </Form.Item>
                <div style={{ color: '#ccc' }}>
                  订单总金额：{this.state.selectedItem?.amount / 100 || 0}元
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
                    批量导入请
                    <a onClick={this.exportModule}>点击下载待发货订单</a>
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
                  action="/guzhe/product_order/express_no/import"
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
                  <div
                    style={{
                      position: 'relative',
                      color: 'rgba(0, 0, 0, 0.427450980392157)',
                    }}
                  >
                    <span>支持扩展名：.xls .xlsx</span>
                  </div>
                </Upload>
              </Form.Item>
            </Form>
          </Modal>
        </Spin>
      </PageContainer>
    );
  }
}

export default orderManage;
