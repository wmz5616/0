import React from 'react';
import { QuestionCircleOutlined, BulbOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Table,
  Select,
  Tooltip,
  Modal,
  InputNumber,
  Alert,
  DatePicker,
} from 'antd';
import { history, connect } from 'umi';

const { Option } = Select;
const { RangePicker } = DatePicker;

class Collection extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    editModalVisible: false,
    selectedRowKeys: [],
    list: [],
    total: 0,
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
        const params = {
          searchField2: this.props.id,
          startTime: this.state.startTime,
          endTime: this.state.endTime,
          keyword: this.state.keyword,
          searchStrField1: this.state.nickName,
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
            }
          },
        });
      },
    );
  };

  onSelectChange = (selectedRowKeys) => {
    this.setState({ selectedRowKeys });
  };

  pageChange = (page) => {
    this.setState(
      {
        pageNum: page,
      },
      () => {
        this.getData();
      },
    );
  };

  editSubmit = () => {
    this.formRefs.current.validateFields().then((values) => {
      const params = {
        orderId: this.state.id,
        refundAmount: values.refundAmount * 100,
        refundReason: values.refundReason,
      };
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: { ...params },
        url: `/ddql/order/recharge/refund`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.setState(
              {
                editModalVisible: false,
                selectedRowKeys: [],
              },
              () => {
                this.getData();
              },
            );
          } else {
            message.info(res.msg);
          }
        },
      });
    });
  };

  onFinish = (vas) => {
    this.setState(
      {
        pageNum: 1,
        selectedRowKeys: [],
        startTime: vas.time ? vas.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        endTime: vas.time ? vas.time[1].format('YYYY-MM-DD 23:59:59') : undefined,
        keyword: vas.keyword,
        nickName: vas.nickName,
        searchIntStatus: vas.searchIntStatus,
      },
      () => {
        this.getData();
      },
    );
  };

  resets = () => {
    this.formRef.current.resetFields();
    this.setState(
      {
        pageNum: 1,
        selectedRowKeys: [],
        startTime: undefined,
        endTime: undefined,
        keyword: undefined,
        nickName: undefined,
        searchIntStatus: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  handleBatchRefund = () => {
    const { selectedRowKeys, list } = this.state;
    if (selectedRowKeys.length !== 1) {
      return message.warning('请选择一条退款记录');
    }
    const record = list.find((item) => item.id === selectedRowKeys[0]);
    if (!record) return;
    if (record.status == 3 || record.status == 4) {
      return message.warning('该记录不可退款');
    }
    this.showRefund(record);
  };

  showRefund = (e) => {
    this.setState({ editModalVisible: true }, () => {
      setTimeout(() => {
        this.formRefs.current.resetFields();
        this.formRefs.current.setFieldsValue({
          refundAmount: this.preciseDivide(e.amount, 100),
        });
        this.setState({
          amount: this.preciseDivide(e.amount, 100),
          id: e.id,
        });
      }, 100);
    });
  };

  export = () => {
    const that = this;
    let xhr = new XMLHttpRequest();
    let fileName = `充值记录列表.xls`;
    xhr.open('POST', `/ddql/order/recharge/export`, true);
    xhr.responseType = 'blob';
    xhr.setRequestHeader('Content-Type', 'application/json');
    const data = {
      startTime: this.state.startTime,
      endTime: this.state.endTime,
      keyword: this.state.keyword,
      searchStrField1: this.state.nickName,
      searchIntStatus: this.state.searchIntStatus,
      searchField2: this.props.id,
    };

    xhr.setRequestHeader('token', localStorage.getItem('token'));
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
            if (typeof a.download === 'undefined') {
              window.location = objectUrl;
            } else {
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

  preciseDivide = (a, b, factor = 100) => {
    return (a * factor) / (b * factor);
  };

  render() {
    const { list = [], selectedRowKeys, pageNum, total } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const columns = [
      {
        title: '序号',
        dataIndex: 'id',
      },
      {
        title: '交易编号',
        dataIndex: 'orderNo',
      },
      {
        title: '操作人姓名',
        dataIndex: 'nickName',
      },
      {
        title: '操作人手机号',
        dataIndex: 'phone',
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
        title: '充值时间',
        dataIndex: 'createTime',
      },
      {
        title: '充值状态',
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
      <div>
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col span={7}>
                  <Form.Item label="关键词" name="keyword">
                    <Input placeholder="请输入手机号码或交易编号或金额" />
                  </Form.Item>
                </Col>
                <Col span={5}>
                  <Form.Item label="操作人姓名" name="nickName">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col span={5}>
                  <Form.Item label="充值状态" name="searchIntStatus">
                    <Select placeholder="请选择" allowClear>
                      <Option value={1}>待付款</Option>
                      <Option value={2}>已完成</Option>
                      <Option value={3}>已取消</Option>
                      <Option value={4}>已退款</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={7}>
                  <Form.Item label="充值时间" name="time">
                    <RangePicker format="YYYY-MM-DD" allowClear={false} />
                  </Form.Item>
                </Col>
                <Col span={17} style={{ textAlign: 'right' }}>
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>
                    <Button className="mL15" onClick={this.resets}>
                      重置
                    </Button>
                    <Button className="mL15" onClick={() => history.push('/PlaceManagement/index')}>
                      返回
                    </Button>
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </div>
          
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row>
              <Col span={6}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>充值记录</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary" onClick={this.handleBatchRefund}>
                    退款
                  </Button>
                  <Button className="mL15" onClick={this.export}>
                    导出
                  </Button>
                </div>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              rowKey="id"
              rowSelection={rowSelection}
              columns={columns}
              dataSource={list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: pageNum,
              }}
            />
          </div>
        </Spin>
        <Modal
          destroyOnClose
          onOk={this.editSubmit}
          open={this.state.editModalVisible}
          title={
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <div style={{ width: 4, height: 20, background: '#1890ff', marginRight: 10 }}></div>
              <h3 style={{ margin: 0 }}>退款</h3>
            </div>
          }
          onCancel={() => {
            this.setState({ editModalVisible: false });
          }}
        >
          <Alert
            message="退款金额默认为充值总金额，最多不能超过充值总金额"
            type="warning"
            showIcon
            icon={<BulbOutlined />}
            style={{ marginBottom: 20 }}
          />
          <Form
            ref={this.formRefs}
            labelCol={{ span: 5 }}
            wrapperCol={{ span: 16 }}
          >
            <Form.Item
              label="退款金额"
              name="refundAmount"
              rules={[{ required: true, message: '请输入' }]}
            >
              <InputNumber min={0} max={this.state.amount} style={{ width: '100%' }} placeholder="请输入" />
            </Form.Item>
            <div
              style={{
                color: 'rgba(0, 0, 0, 0.45)',
                paddingLeft: '21%',
                marginTop: -15,
                marginBottom: 15,
              }}
            >
              充值总金额(元)：{this.state.amount}
            </div>
            <Form.Item
              label="退款原因"
              name="refundReason"
              rules={[{ required: true, message: '请输入' }]}
            >
              <Input.TextArea placeholder="请输入" rows={4} />
            </Form.Item>
          </Form>
        </Modal>
      </div>
    );
  }
}

export default connect()(Collection);
