import React from 'react';
import { UserOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Modal,
  Popconfirm,
  Table,
  Select,
  DatePicker,
  Tabs,
  Menu,
  Tooltip,
  Dropdown,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
const { TabPane } = Tabs;
import ActivateABankCard from './components/ActivateABankCard';
import { handleExport } from '../../utils/utils';
import { getToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;

//充值管理

class Login extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    xxx: true,
    selectedRowKeys: [],
    aa: [],
    statistical: {
      todayStatData: {},
      totalStatData: {},
    },
  };

  componentDidMount() {
    const { phone } = this.props.location.query;
    if (phone) {
      this.formRef.current.setFieldsValue({
        search: phone,
      });
      this.setState(
        {
          search: phone,
        },
        () => {
          this.getData();
        },
      );
    } else {
      this.getData();
    }
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        const params = {
          startTime: this.state.startTime,
          endTime: this.state.endTime,
          keyword: this.state.keyword,
          searchIntStatus: this.state.searchIntStatus,
          pageNum: this.state.pageNum,

          // limit: 999999,
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
                list: res.data.list,
                total: res.data.total,
              });
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });
        //获取统计数据
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {},
          url: `/ddql/order/recharge/stat`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
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

  pageChange = (page) => {
    //列表改变页码
    this.setState(
      {
        pageNum: page,
        aa: this.state.selectedRowKeys,
      },
      () => {
        this.getData();
      },
    );
  };

  callback = (key) => {
    this.setState(
      {
        xxx: false,
        pageNum: 1,
      },
      () => {
        this.setState({
          xxx: true,
        });
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

  onChangez = (value, dateString) => {
    console.log('Formatted Selected Time: ', dateString);
    this.setState({
      time_arr: dateString[0] !== '' && [`${dateString[0]} 00:00`, `${dateString[1]} 23:59`],
      page: 1,
    });
  };

  //查询
  onFinish = (vas) => {
    this.setState(
      {
        pageNum: 1,
        selectedRowKeys: [],
        selectedRecord: [],
        startTime: vas.time ? vas.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        endTime: vas.time ? vas.time[1].format('YYYY-MM-DD 23:59:59') : undefined,
        keyword: vas.keyword,
        searchIntStatus: vas.searchIntStatus,
      },
      () => {
        this.getData();
      },
    );
  };

  //重置
  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        pageNum: 1,
        selectedRowKeys: [],
        selectedRecord: [],
        startTime: undefined,
        endTime: undefined,
        keyword: undefined,
        searchIntStatus: undefined,
        pageNum: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  onChangeS = (value) => {
    const { dispatch } = this.props;
    this.setState(
      {
        loading: true,
      },
      () => {
        this.downLoad();
      },
    );
  };

  Select = (value) => {
    if (this.state.selectedRowKeys.length == 0) {
      message.error('请选择需要操作的充值记录');
      return;
    }
    const { dispatch } = this.props;
    this.setState(
      {
        loading: true,
      },
      () => {
        this.downLoads();
      },
    );
  };

  export = () => {
    const that = this;
    let xhr = new XMLHttpRequest();
    let fileName = `充值记录列表.xls`; // 文件名称
    xhr.open('POST', `/ddql/order/recharge/export`, true);
    xhr.responseType = 'blob';
    xhr.setRequestHeader('Content-Type', 'application/json');
    const data = {
      startTime: this.state.startTime,
      endTime: this.state.endTime,
      keyword: this.state.keyword,
      searchIntStatus: this.state.searchIntStatus,
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
  deletes = (ids) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        id: ids,
      },
      url: `/api/admin/top_up/order/refund`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 200) {
          message.success(res.message);
          this.getData();
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  preciseDivide = (a, b, factor = 100) => {
    return (a * factor) / (b * factor);
  };

  showRefund = (e) => {
    this.setState({ editModalVisible: true }, () => {
      setTimeout(() => {
        this.formRefs.current.setFieldsValue({
          refundAmount: this.preciseDivide(e.amount, 100),
        });
        this.setState({
          amount: this.preciseDivide(e.amount, 100),
          id: e.id,
        });
      }, 300);
    });
  };

  render() {
    const { selectedRowKeys, total, pageNum, statistical = {} } = this.state;
    console.log(selectedRowKeys);
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
        });
      },
      onSelect: (record, selected, selectedRows, nativeEvent) => {
        if (!selected) {
          this.setState(
            {
              selectedRowKeys: this.state.selectedRowKeys.filter((res) => res != record.id),
            },
            () => {},
          );
        } else {
          console.log(record, selected, selectedRows, nativeEvent);
          this.setState(
            {
              selectedRowKeys: [
                ...this.state.aa,
                ...selectedRows.filter((res) => res).map((res) => res.id),
              ],
            },
            () => {},
          );
        }
      },
    };

    const menu = (
      <Menu>
        <Menu.Item key="1" onClick={this.onChangeS}>
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
        title: '充值团体',
        dataIndex: 'teamName',
      },
      {
        title: '充值时间',
        dataIndex: 'createTime',
      },
      {
        title: '充值状态',
        fixed: 'right',
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
      {
        title: '操作',
        render: (res, record) => (
          <div>
            {record.status != 4 && record.status != 3 && (
              <div className="clickFont" onClick={() => this.showRefund(record)}>
                退款
              </div>
            )}
          </div>
        ),
      },
    ];

    return (
      <div className="zxcv">
        <div className="asd">
          <PageContainer
            header={{
              title: ``,
            }}
          >
            <Spin spinning={this.state.spinning}>
              <div>
                <div style={{ backgroundColor: '#fff' }}>
                  <div style={{ textAlign: 'right', paddingRight: 20, paddingTop: 15 }}>
                    <div
                      style={{
                        display: 'inline-block',
                        padding: '0 25px',
                        borderRight: '1px solid #eaeaea',
                      }}
                    >
                      <div style={{ color: '#929292', fontSize: 18 }}>今日充值订单数</div>
                      <div style={{ fontSize: 18 }}>
                        {statistical.todayStatData.orderNum || 0}笔
                      </div>
                    </div>
                    <div
                      style={{
                        display: 'inline-block',
                        padding: '0 25px',
                        borderRight: '1px solid #eaeaea',
                      }}
                    >
                      <div style={{ color: '#929292', fontSize: 18 }}>今日充值总额</div>
                      <div style={{ fontSize: 18 }}>
                        {statistical.todayStatData.amount / 100 || 0}元
                      </div>
                    </div>
                    <div style={{ display: 'inline-block', padding: '0 0 0 25px' }}>
                      <div style={{ color: '#929292', fontSize: 18 }}>累计充值金额</div>
                      <div style={{ fontSize: 18 }}>
                        {statistical.totalStatData.amount / 100 || 0}元
                      </div>
                    </div>
                  </div>

                  <Tabs
                    defaultActiveKey="1"
                    onChange={this.callback}
                    style={{ marginTop: '-20px' }}
                  >
                    <TabPane tab="充值记录" key="1">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          <div style={{ backgroundColor: '#f0f2f5', paddingBottom: 20 }}>
                            <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px' }}>
                              <Form ref={this.formRef} onFinish={this.onFinish}>
                                <Row gutter={16}>
                                  <Col className="gutter-row" span={5}>
                                    <Form.Item label="关键词" name="keyword">
                                      <Input placeholder="请输入手机号码或交易编号" />
                                    </Form.Item>
                                  </Col>

                                  <Col className="gutter-row" span={4}>
                                    <Form.Item label="充值状态" name="searchIntStatus">
                                      <Select placeholder="请选择">
                                        <Option value={1}>待付款</Option>
                                        <Option value={2}>已完成</Option>
                                        <Option value={3}>已取消</Option>
                                        <Option value={4}>已退款</Option>
                                      </Select>
                                    </Form.Item>
                                  </Col>

                                  <Col className="gutter-row" span={5}>
                                    <Form.Item label="充值时间" name="time">
                                      <RangePicker format="YYYY-MM-DD" onChange={this.onChangez} />
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
                          </div>

                          <div style={{ padding: 24 }}>
                            <Row>
                              <Col span={12}>
                                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>充值记录</h1>
                              </Col>
                              <Col span={12} style={{ textAlign: 'right' }}>
                                <Button className="mL15" onClick={() => this.export()}>
                                  导出
                                </Button>
                              </Col>
                            </Row>

                            <Table
                              style={{ marginTop: 15 }}
                              rowSelection={false}
                              rowKey="id"
                              columns={columns}
                              dataSource={this.state.list}
                              pagination={{
                                showSizeChanger: false,
                                onChange: this.pageChange,
                                pageSize: 10,
                                total: this.state.total,
                                current: pageNum,
                              }}
                              // scroll={{ x: '100%' }}
                            />
                          </div>
                        </div>
                      </div>
                    </TabPane>
                    <TabPane tab="充值配置" key="2">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          {this.state.xxx && <ActivateABankCard />}
                        </div>
                      </div>
                    </TabPane>
                  </Tabs>
                </div>
                <Modal
                  destroyOnClose
                  onOk={this.editSubmit}
                  open={this.state.editModalVisible}
                  title={
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: 35 }}>
                      <div
                        style={{ width: 4, height: 20, background: '#1890ff', marginRight: 10 }}
                      ></div>
                      <h3 style={{ margin: 0 }}>退款</h3>
                    </div>
                  }
                  onCancel={() => {
                    this.setState({ editModalVisible: false });
                  }}
                >
                  <Form
                    ref={this.formRefs}
                    labelCol={{ span: 4 }}
                    wrapperCol={{ span: 16 }}
                    initialValues={{
                      status: true, // 默认启用状态为开启
                    }}
                  >
                    {/* 项目名称 */}
                    <Form.Item
                      label="退款金额"
                      name="refundAmount"
                      rules={[{ required: true, message: '请输入' }]}
                    >
                      <Input placeholder="请输入" />
                    </Form.Item>
                    <div
                      style={{
                        color: 'rgba(0, 0, 0, 0.427)',
                        paddingLeft: '17%',
                        marginTop: -10,
                        marginBottom: 10,
                      }}
                    >
                      充值总金额(元)：{this.state.amount}
                    </div>
                    {/* 介绍 */}
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
            </Spin>
          </PageContainer>
        </div>
      </div>
    );
  }
}

export default connect()(Login);
