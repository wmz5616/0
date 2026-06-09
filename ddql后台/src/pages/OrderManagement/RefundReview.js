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
  Radio,
  Table,
  Select,
  Modal,
  DatePicker,
  Menu,
  Dropdown,
  InputNumber,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import ExportJsonExcel from 'js-export-excel';
import { history, connect } from 'umi';
import OrderDetailss from './components/OrderDetailss';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import moment from 'moment';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;
//退款审核
//退款审核
//退款审核
//退款审核
class RefundReview extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: Number(this.props.location.query.pageNum ? this.props.location.query.pageNum : 1),
    confirmLoading: false,
    xxx: false,
    xx: [],
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
            startTime: this.state.startTime,
            endTime: this.state.endTime,
            pageNum: this.state.pageNum,
          },
          url: `/ddql/order/exchange/refund/apply/lists`,
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
      },
    );
  };

  onSelectChange = (selectedRowKeys, xx) => {
    //触发表单筛选
    console.log('selectedRowKeys changed: ', selectedRowKeys);
    this.setState({
      selectedRowKeys,
      xx,
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

  showModal = (data, e) => {
    if (data) {
      this.setState(
        {
          NewRoles: true,
          selectorderNoList: data,
          amount: e.amount,
          record: e,
        },
        () => {
          this.formRefs.current.setFieldsValue({
            refundAmount: e.amount,
          });
        },
      );
    } else {
      message.error('请选择需要审核的内容');
    }
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
        () => {
          this.props.dispatch({
            type: 'myModel/getSetData',
            payload: {
              applyId: this.state.record.id,
              status: values.status,
              remark: values.remark,
              refundAmount: values.refundAmount,
            },
            method: 'POST',
            url: `/ddql/order/exchange/refund/audit`,
            myData: (res) => {
              this.setState({
                confirmLoading: false,
              });
              if (res.code === 10000) {
                message.success(res.msg);
                this.setState({
                  NewRoles: false,
                  selectedRowKeys: [],
                  // pageNum: 1,
                });
                this.getData();
              } else {
                message.error(res.msg);
              }
            },
          });
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
        xx: [],
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

  aperto = (x) => {
    this.setState({
      xxx: x,
    });
  };

  render() {
    const { refundlist = [], selectedRowKeys, NewRoles, xxx, stadiumList = [] } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
      getCheckboxProps: (record) => ({
        disabled: record.status == 1, // 只有待审核状态可以选
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
      },

      {
        title: '商品',
        dataIndex: 'productName',
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
        title: '备注',
        dataIndex: 'remark',
      },
      {
        title: '申请时间',
        dataIndex: 'createTime',
      },
      {
        title: '申请状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <span style={{ color: text == 1 ? '#1890ff' : text == 2 ? '#009900' : '#FF0000' }}>
                {text == 1 ? '待审核' : text == 2 ? '已通过' : '已驳回'}
              </span>
            </div>
          );
        },
      },
      {
        title: '操作',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModalss({ ...record })}>详情</a>
              {record.status == 1 && (
                <a className="mL15" onClick={() => this.showModal([record.orderNo], record)}>
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
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
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

                <Col className="gutter-row" span={6} style={{ textAlign: 'right' }}>
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
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>退款审核</h1>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              loading={this.state.loading}
              rowKey="orderNo"
              rowSelection={false}
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
                    <InputNumber min={0} />
                  </Form.Item>
                  <div style={{ color: '#ccc' }}>订单总金额(元)：{this.state.amount}</div>
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
                  rules={[{ required: this.state.sss == 2 ? false : true, message: '请输入' }]}
                >
                  <TextArea rows={4} placeholder="请输入" />
                </Form.Item>
              </Form>
            </Modal>
            {this.state.detailss && (
              <OrderDetailss handleCancels={this.handleCancels} applyInfo={this.state.applyInfo} />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(RefundReview);
