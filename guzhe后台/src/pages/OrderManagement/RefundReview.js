import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Modal,
  InputNumber,
  Radio,
  Row,
  Select,
  Spin,
  Table,
} from 'antd';
import React from 'react';
import { history } from '@umijs/max';
import OrderDetailss from './components/OrderDetailss';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;
//退款审核
class RefundReview extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    confirmLoading: false,
    pageNum: 1,
    selectedRowKeys: [],
    record: {},
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      async () => {
        const res = await post(`/guzhe/product_order/refund/apply/lists`, {
          searchIntStatus: this.state.searchIntStatus,
          searchStrField1: this.state.searchStrField1,
          keyword: this.state.keyword,
          startTime: this.state.startTime,
          endTime: this.state.endTime,
          pageNum: this.state.pageNum,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          this.setState({
            list: res.data.list,
            total: res.data.total,
          });
        } else {
          message.error(res?.msg);
        }
      },
    );
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({
      selectedRowKeys,
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

  showModal = (record) => {
    this.setState(
      {
        NewRoles: true,
        record,
        sss: 2, //默认审核通过
      },
      () => {
        this.formRefs.current.setFieldsValue({
          refundAmount: record.refundAmount ? record.refundAmount / 100 : record.amount / 100,
          remark: record.remark,
          // status: record.status,
          status: 2, //默认审核通过
        });
      },
    );
  };

  showModalss = (data) => {
    this.setState({
      applyInfo: data,
      detailss: true,
    });
  };

  handleOk = () => {
    this.formRefs.current.validateFields().then((values) => {
      this.setState(
        {
          confirmLoading: true,
        },
        async () => {
          const res = await post(`/guzhe/product_order/refund/audit`, {
            applyId: this.state.record.id,
            status: values.status,
            remark: values.remark,
            refundAmount: values.refundAmount * 100,
          });
          this.setState({
            confirmLoading: false,
          });
          if (res.code == 10000) {
            message.success(res.msg);
            this.setState({
              NewRoles: false,
              selectedRowKeys: [],
              record: {},
            });
            this.getData();
          } else {
            message.error(res?.msg);
          }
        },
      );
    });
  };

  handleCancel = () => {
    this.setState({
      NewRoles: false,
      confirmLoading: false,
    });
    this.formRefs.current.resetFields();
  };

  handleCancels = () => {
    this.setState({
      detailss: false,
    });
  };

  // 删除函数
  deletes = (ids) => {
    console.log(ids);
  };

  onFinish = (v) => {
    this.setState(
      {
        pageNum: 1,
        startTime: v.time ? v.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        searchIntStatus: v.searchIntStatus,
        searchStrField1: v.searchStrField1,
        keyword: v.keyword,
        endTime: v.time ? v.time[1].format('YYYY-MM-DD 23:59:59') : undefined,
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
        startTime: undefined,
        searchIntStatus: undefined,
        searchStrField1: undefined,
        keyword: undefined,
        endTime: undefined,
        pageNum: 1,
        selectedRowKeys: [],
      },
      () => {
        this.getData();
      },
    );
  };

  onChange = (value, dateString) => {
    this.setState({
      startTime: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
      endTime: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
    });
  };

  onChangezz = (e) => {
    this.setState({
      sss: e.target.value,
    });
  };

  render() {
    const {
      selectedRowKeys,
      NewRoles,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
      getCheckboxProps: (record) => ({
        disabled: record.status != 1, // 只有待审核状态可以选
      }),
    };

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '订单号',
        dataIndex: 'orderNo',
        render: (res, record) => (
          <div
            style={{
              color: '#1890ff',
              cursor: 'pointer',
            }}
            onClick={() =>
              history.push(`/OrderManagement/OrderDetails?id=${record.orderId}`)
            }
          >
            {res}
          </div>
        ),
      },

      {
        title: '商品',
        dataIndex: 'productName',
      },
      {
        title: '商家',
        dataIndex: 'shopName',
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
        with: 120,
        dataIndex: 'amount',
        render: (res) => <div>{res / 100 || 0}元</div>,
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
        title: '申请状态',
        width: 120,
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <span
                style={{
                  color:
                    text == 1 ? '#1890ff' : text == 2 ? '#009900' : '#FF0000',
                }}
              >
                {text == 1 ? '待审核' : text == 2 ? '已通过' : '已驳回'}
              </span>
            </div>
          );
        },
      },
      {
        title: '操作',
        width: 120,
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModalss({ ...record })}>详情</a>
              {record.status == 1 && (
                <a
                  className="mL15"
                  onClick={() => this.showModal(record)}
                >
                  审核
                </a>
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
          <div
            style={{
              backgroundColor: '#fff',
              padding: '20px 20px 0 20px',
              marginBottom: 15,
            }}
          >
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="订单编号" name="searchStrField1">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <>
                  <Col className="gutter-row" span={4}>
                    <Form.Item label="下单人" name="keyword">
                      <Input placeholder="下单人姓名、手机号" />
                    </Form.Item>
                  </Col>
                  <Col className="gutter-row" span={4}>
                    <Form.Item label="申请状态" name="searchIntStatus">
                      <Select allowClear showSearch placeholder="请输入">
                        <Option value={1}>待审核</Option>
                        <Option value={2}>已通过</Option>
                        <Option value={3}>已驳回</Option>
                      </Select>
                    </Form.Item>
                  </Col>
                  <Col className="gutter-row" span={6}>
                    <Form.Item label="申请时间" name="time">
                      <RangePicker
                        style={{ width: '100%' }}
                        // showTime={{ format: 'HH:mm' }}
                        format="YYYY-MM-DD"
                      />
                    </Form.Item>
                  </Col>
                </>

                <Col
                  className="gutter-row"
                  span={6}
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
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row gutter={16}>
              <Col span={6}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  退款审核
                </h1>
              </Col>
              <Col
                span={18}
                style={{ display: 'flex', justifyContent: 'flex-end', gap: 10 }}
              >
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
                    const record = this.state.list.find(item => item.id == this.state.selectedRowKeys[0]);
                    this.showModal(record);
                  }}
                >
                  审核
                </Button>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              loading={this.state.loading}
              rowKey="id"
              rowSelection={rowSelection}
              columns={columns}
              dataSource={this.state.list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total: this.state.total,
                current: this.state.pageNum,
              }}
            // scroll={{ x: '100%' }}
            />

            <Modal
              title="审核"
              visible={NewRoles}
              onOk={this.handleOk}
              onCancel={this.handleCancel}
              confirmLoading={this.state.confirmLoading}
            >
              <Form ref={this.formRefs}>
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
                    <InputNumber min={0} max={this.state.record.amount / 100} />
                  </Form.Item>
                  <div style={{ color: '#ccc' }}>
                    订单总金额：{this.state.record.amount / 100 || 0}元
                  </div>
                </Form.Item>
                <Form.Item
                  label="审核结果"
                  name="status"
                  initialValue={2}
                  rules={[{ required: true, message: '请选择!' }]}
                >
                  <Radio.Group onChange={this.onChangezz}>
                    <Radio value={2}>通过</Radio>
                    <Radio value={3}>驳回</Radio>
                  </Radio.Group>
                </Form.Item>

                <Form.Item
                  label="审核意见"
                  name="remark"
                  rules={[
                    {
                      required: this.state.sss == 2 ? false : true,
                      message: '请输入',
                    },
                  ]}
                >
                  <TextArea rows={4} placeholder="请输入" />
                </Form.Item>
              </Form>
            </Modal>
            {this.state.detailss && (
              <OrderDetailss
                handleCancels={this.handleCancels}
                applyInfo={this.state.applyInfo}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default RefundReview;
