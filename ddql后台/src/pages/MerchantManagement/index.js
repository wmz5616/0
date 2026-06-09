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
  Radio,
  DatePicker,
  Switch,
  Popover,
  Tag,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import NewNotice from './components/NewNotice';
import { DownOutlined, UpOutlined } from '@ant-design/icons';

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
      () => {
        //列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            keyword: this.state.keyword,
            searchType: this.state.mainType,
            searchField1: this.state.acc_type,
            searchStrField1: this.state.searchStrField1,
            pageSize: 10,
            pageNum: this.state.pageNum,
          },
          url: `/ddql/merchant/select`,
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
        // 场所列表
        // this.props.dispatch({
        //   type: 'myModel/getSetData',
        //   payload: {
        //     limit: 9999,
        //   },
        //   url: `/api/admin/stadium/lists`,
        //   method: 'GET',
        //   myData: (res) => {
        //     if (res && res.code === 200) {
        //       this.setState({
        //         placeList: res.data.lists,
        //       });
        //     } else {
        //       message.error(res.message);
        //     }
        //   },
        // });
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

  showModal = (add, record) => {
    this.setState({
      NewRoles: true,
      add,
      edit: record,
    });
  };

  handleOk = () => {
    this.setState({
      NewRoles: false,
    });
  };

  //删除
  deletes = (ids) => {
    if (ids == undefined) {
      message.error('请选择需要删除的公告');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          id: ids.join(','),
        },
        url: `/api/admin/notice/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            message.success(res.msg);
            this.getData();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
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
        // publish_start_time:res.publish_start_time,
        // publish_end_time:res.publish_end_time,
      },
      () => {
        this.getData();
      },
    );
  };

  siteFrontReserve = (v, ids) => {
    if (ids.length == 0) {
      message.error('请选择需要操作的数据');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          searchIntStatus: v ? 1 : 0,
          searchIds: ids,
        },
        url: `/ddql/merchant/status/set`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            this.setState({
              selectedRowKeys: [],
            });
            message.success(res.msg);
            this.getData();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
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

  onChangez = (value, dateString) => {
    this.setState({
      time_arr: dateString[0] !== '' && [`${dateString[0]} 00:00`, `${dateString[1]} 23:59`],
      page: 1,
    });
  };

  aperto = (x) => {
    this.setState({
      xxx: x,
    });
  };

  render() {
    const { list = [], selectedRowKeys, NewRoles, edit, xxx, placeList = [] } = this.state;

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
              {/* {record.type==1&&'企业'}
              {record.type==0&&'个人经营者'} */}
              {record.mainType == 10030 && <span>企业商户</span>}
              {record.mainType == 10031 && <span>个体工商户</span>}
              {record.mainType == 10034 && <span>民办非企业</span>}
              {record.mainType == 10033 && <span>事业单位</span>}
            </div>
          );
        },
      },
      {
        title: '关联门店',
        dataIndex: 'shopList',
        render: (text, record) => {
          return <div>{record.shopList && record.shopList.map((x) => x.namne).join('、')}</div>;
        },
        // render: (text, record) => {
        //   return (
        //     <Popover
        //       content={
        //         <div style={{ maxWidth: '280px' }}>
        //           {record.stadiums_info &&
        //             record.stadiums_info.map((res) => (
        //               <Tag style={{ marginBottom: '10px' }}>{res.name}</Tag>
        //             ))}
        //         </div>
        //       }
        //       trigger="hover"
        //       placement="top"
        //     >
        //       <div
        //         // onClick={() => {
        //         //   this.setState({
        //         //     ddd: record.stadium_ids.split(/,/),
        //         //   });
        //         // }}
        //         style={{ color: '#1890FF', cursor: 'pointer' }}
        //       >
        //         {!record.stadiums_info ? (
        //           ''
        //         ) : record.stadiums_info.length <= 1 ? (
        //           record.stadiums_info[0].name
        //         ) : (
        //           <span style={{ color: '#1890ff' }}>共计{record.stadiums_info.length}个场所</span>
        //         )}
        //       </div>
        //     </Popover>
        //   );
        // },
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
              {record.accType == 10070 ? '对公账户' : ''}
              {record.accType == 10071 ? '法人账户' : ''}
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
              {record.applicationStatus == 'COMPLETED' ? (
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
              {record.applicationStatus == 'REVIEWING' && (
                <Link
                  to={`ProgressQuery?stadium_id=${record.stadium_id}&detail=${JSON.stringify(
                    record,
                  )}&name=${record.name}&application_no=${
                    record.application_no
                  }&application_status=${record.application_status}&created_at=${
                    record.created_at
                  }`}
                >
                  申请审核中
                </Link>
              )}
              {record.applicationStatus == 'REVIEW_BACK' && (
                <Link
                  to={`UpdateMerchants?stadium_id=${record.stadium_id}&detail=${JSON.stringify(
                    record,
                  )}&id=${record.id}`}
                >
                  <span style={{ color: 'red' }}>申请已驳回</span>
                </Link>
              )}
              {record.applicationStatus == 'AGREEMENT_SIGNING' && (
                <Link
                  to={`ProgressQuery?stadium_id=${record.stadium_id}&detail=${JSON.stringify(
                    record,
                  )}&name=${record.name}&application_no=${
                    record.application_no
                  }&application_status=${record.application_status}&created_at=${
                    record.created_at
                  }`}
                >
                  <a>协议待签署</a>
                </Link>
              )}
              {record.applicationStatus == 'BUSINESS_OPENING' && (
                <Link
                  to={`ProgressQuery?stadium_id=${record.stadium_id}&detail=${JSON.stringify(
                    record,
                  )}&name=${record.name}&application_no=${
                    record.application_no
                  }&application_status=${record.application_status}&created_at=${
                    record.created_at
                  }`}
                >
                  <span style={{ color: '#e3dc14fe' }}>业务开通中</span>
                </Link>
              )}
              {record.applicationStatus == 'COMPLETED' && (
                <Link
                  to={`ProgressQuery?stadium_id=${record.stadium_id}&detail=${JSON.stringify(
                    record,
                  )}&name=${record.name}&application_no=${
                    record.application_no
                  }&application_status=${record.application_status}&created_at=${
                    record.created_at
                  }`}
                >
                  <span style={{ color: '#03bf16fe' }}>申请已完成</span>
                </Link>
              )}

              {record.applicationStatus == undefined && (
                <Link
                  to={`UpdateMerchants?stadium_id=${record.stadium_id}&detail=${JSON.stringify(
                    record,
                  )}&id=${record.id}`}
                >
                  <span style={{ color: 'red' }}>数据异常，请修改</span>
                </Link>
              )}

              {(record.applicationStatus == 'WAIT_SUBMIT' || record.status == 4) && (
                <Link to={`UpdateMerchants?detail=${JSON.stringify(record)}&id=${record.id}`}>
                  <a>申请待提交</a>
                </Link>
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
            <div>
              <Link to={`MerchantDetails?detail=${JSON.stringify(record)}`}>详情</Link>
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
                {/* <Col className="gutter-row" span={5}>
                  <Form.Item label="选择场所" name="stadium_id">
                    <Select
                      allowClear
                      showSearch
                      optionFilterProp="label"
                      placeholder="请选择需要开通商户功能的场所"
                    >
                      {placeList.map((res) => {
                        return (
                          <Option value={res.id} key={res.id} label={`${res.id}${res.name}`}>
                            {res.name}
                          </Option>
                        );
                      })}
                    </Select>
                  </Form.Item>
                </Col> */}
                {/* <Col className="gutter-row" span={4}>
                  <Form.Item label="主体类型" name="type">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>个人经营者</Option>
                      <Option value={2}>企业商户</Option>
                      <Option value={3}>个体工商户</Option>
                      <Option value={4}>事业单位</Option>
                      <Option value={5}>民办非企业</Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={4}>
                  <Form.Item label="账户类型" name="top">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>个人账户</Option>
                      <Option value={0}>对公账户</Option>
                      <Option value={3}>法人账户</Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={3}>
                  <Form.Item label="账户状态" name="publish">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>进件申请中</Option>
                      <Option value={0}>已开通</Option>
                    </Select>
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={3}>
                  <Form.Item label="启用状态" name="publish">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>是</Option>
                      <Option value={0}>否</Option>
                    </Select>
                  </Form.Item>
                </Col>

                {xxx && (
                  <>
                    <Col className="gutter-row" span={4}>
                      <Form.Item label="进件状态" name="publish">
                        <Select allowClear placeholder="请选择">
                          <Option value={1}>审批中</Option>
                          <Option value={0}>已完成</Option>
                          <Option value={2}>已驳回</Option>
                          <Option value={2}>待提交</Option>
                        </Select>
                      </Form.Item>
                    </Col>

                    <Col className="gutter-row" span={6}>
                      <Form.Item label="开通时间" name="usernames">
                        <RangePicker format="YYYY-MM-DD" onChange={this.onChangez} />
                      </Form.Item>
                    </Col>
                  </>
                )} */}

                <Col className="gutter-row" span={6} style={{ textAlign: 'right' }}>
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>

                    <Button className="mL15" onClick={this.resets}>
                      重置
                    </Button>
                    {/* {xxx == false ? (
                      <a style={{ paddingLeft: 10 }} onClick={() => this.aperto(true)}>
                        展开 <DownOutlined />
                      </a>
                    ) : (
                      <a style={{ paddingLeft: 10 }} onClick={() => this.aperto(false)}>
                        收起 <UpOutlined />
                      </a>
                    )} */}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </div>
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>商户管理</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button type="primary">
                    <Link to={`NewMerchants`}>+新增商户</Link>
                  </Button>

                  <Button
                    className="mL15 bxluSe"
                    onClick={() => this.siteFrontReserve(true, this.state.selectedRowKeys)}
                  >
                    启用
                  </Button>
                  <Button
                    className="mL15 bxHuang"
                    onClick={() => this.siteFrontReserve(false, this.state.selectedRowKeys)}
                  >
                    禁用
                  </Button>

                  {/* <Popconfirm
                    title={
                      <>
                        <div>删除提示</div>
                        <div>
                          <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                          <span style={{ color: '#ccc' }}>确定删除吗？</span>
                        </div>
                      </>
                    }
                    onConfirm={() => this.deletes(selectedRowKeys)}
                    // onCancel={cancel}
                    okText="是"
                    cancelText="否"
                  >
                    <Button danger className="mL15">
                      删除
                    </Button>
                  </Popconfirm> */}
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

export default connect()(NoticeNotice);
