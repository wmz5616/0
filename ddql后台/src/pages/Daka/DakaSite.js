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
  Modal,
  Table,
  Select,
  DatePicker,
  InputNumber,
  Popover,
  Space,
  Switch,
  Radio,
  Popconfirm,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import ExportJsonExcel from 'js-export-excel';
import moment from 'moment';
import NewVenues from './components/AddShangquanModal';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;

class DakaSite extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    list: [],
    xxx: false,
    selectedRecord: [],
    selectedRowKeys: [],
    pageNum: 1,
    www: [],
    serviceLists: [],
    modalVisible: false,
    adminList: [],
    gymTypelist: [],
    adminListf: [],
  };

  componentDidMount() {
    this.getData();
    this.getAdminList(1);
    this.getTypeList();
  }

  getTypeList = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {},
      url: `/ddql/checkInType/getCheckInTypeList`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            gymTypelist: res.data,
          });
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  getAdminList = (e) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        pageSize: 30,
        pageNum: e,
      },
      url: `/ddql/common/user/lists`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState(
            {
              adminList: this.state.adminList.concat(res.data.list),
            },
            () => {
              if (res.data.total > this.state.adminList.length) {
                this.getAdminList((e += 1));
              }
            },
          );
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
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
        //列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchField2: this.state.searchField2,
            searchType: this.state.searchType,
            searchIntStatus: this.state.searchIntStatus,
            searchField1: this.state.searchField1,
            keyword: this.state.keyword,
            startTime: this.state.start_time,
            endTime: this.state.end_time,
            // created_at: this.state.created_at, //创建时间
            pageNum: this.state.pageNum,
            pageSize: 10,
          },
          url: `/ddql/checkInPlace/select`,
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

  onFinish = (vas) => {
    this.setState(
      {
        searchField2: vas.searchField2,
        searchType: vas.searchType,
        searchIntStatus: vas.searchIntStatus,
        searchField1: vas.searchField1,
        keyword: vas.keyword,
        start_time: vas.time ? vas.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        end_time: vas.time ? vas.time[1].format('YYYY-MM-DD 23:59:59') : undefined,
        // order_no: vas.order_no, //订单编号
        // gym_id: vas.gym_id, //场馆
        // order_type: vas.order_type, //订单类型
        // order_way: vas.order_way, //下单方式
        // pay_way: vas.pay_way, //方式支付
        // phone: vas.phone, //下单手机号
        // status: vas.status, //订单状态
        // ticket_no: vas.ticket_no, //券码
        // created_at: vas.created_at, //创建时间

        // start_time: this.state.start_time,
        // end_time: this.state.end_time,
        // pageNum: 1,
        // selectedRowKeys: [],
        // selectedRecord: [],
        // www: [],
        // aa: [],
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
        searchType: undefined,
        searchIntStatus: undefined,
        searchField2: undefined,
        is_front: undefined,
        searchField1: undefined,
        keyword: undefined,
        start_time: undefined,
        end_time: undefined,
        selectedRowKeys: [],
        selectedRecord: [],
      },
      () => {
        this.getData();
      },
    );
  };

  onChange = (value, dateString) => {
    console.log(dateString);
    this.setState({
      start_time: dateString[0] != '' ? `${dateString[0]} 00:00:00` : undefined,
      end_time: dateString[1] != '' ? `${dateString[1]} 23:59:59` : undefined,
    });
  };

  aperto = (x) => {
    this.setState({
      xxx: x,
    });
  };

  // 开关场所前台展示
  front = (checked, id, name) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: {
        status: checked ? 0 : 1,
        changeIds: [id],
      },
      method: 'POST',
      url: `/ddql/checkInPlace/status/set`,
      myData: (res) => {
        if (res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  // 开关场所对外开放
  offer = (checked, id) => {
    if (!id) {
      message.error('请选择场所');
    } else {
      this.setState(
        {
          ids: id.join(','),
          checked,
        },
        () => {
          if (checked) {
            const { dispatch } = this.props;
            dispatch({
              type: 'myModel/getSetData',
              payload: {
                id: id.join(','),
                is_open: this.state.checked,
              },
              method: 'POST',
              url: `/api/admin/stadium/open`,
              myData: (res) => {
                if (res.code === 200) {
                  message.info(res.message);
                  this.getData();
                } else {
                  message.error(res.message);
                }
              },
            });
          } else {
            this.setState({
              isModalOpen: true,
            });
          }
        },
      );
    }
  };

  // 开关场所置顶
  Top = (checked, id) => {
    if (!id) {
      message.error('请选择场所');
    } else {
      this.setState(
        {
          checked,
        },
        () => {
          const { dispatch } = this.props;
          dispatch({
            type: 'myModel/getSetData',
            payload: {
              id: id.join(','),
              is_top: this.state.checked,
            },
            method: 'POST',
            url: `/api/admin/stadium/top`,
            myData: (res) => {
              if (res.code === 200) {
                message.info(res.message);
                this.getData();
              } else {
                message.error(res.message);
              }
            },
          });
        },
      );
    }
  };

  // 关闭原因

  onChangexx = (v) => {
    console.log(v.target.value);
    this.setState({
      closureReason: v.target.value,
      customReason: undefined,
    });
  };

  CustomReason = (v) => {
    this.setState({
      customReason: v.target.value,
    });
  };

  handleCancel = () => {
    this.setState({
      isModalOpen: false,
    });
  };

  handleOk = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: {
        changeId: this.state.id,
        reason: this.state.customReason,
        status2: 0,
      },
      method: 'POST',
      url: `/ddql/shop/setStatus`,
      myData: (res) => {
        if (res.code === 10000) {
          message.success(res.msg);
          this.setState({
            isModalOpen: false,
          });
          this.getData();
        } else {
          message.error(res.message);
        }
      },
    });
  };

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    // console.log('selectedRowKeys changed: ', selectedRowKeys);
    this.setState({ selectedRowKeys });
  };

  //删除
  deletes = (ids) => {
    if (ids.length == 0) {
      message.error('请选择需要删除的数据');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          deleteIds: ids,
        },
        url: `/ddql/checkInPlace/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code == 10000) {
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

  findAdmin = (e) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: e,
      },
      url: `/ddql/checkInPlace/selectUserByPlaceId`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code == 10000) {
          this.setState({
            adminListf: res.data,
          });
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  render() {
    const {
      gymTypelist = [],
      selectedRowKeys,
      NewRoles,
      total,
      pageNum,
      gymList = [],
      xxx,
      communities = [],
      motionTypeLists = [],
    } = this.state;
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
        title: '场地名称',
        dataIndex: 'name',
      },
      {
        title: '场地地址',
        dataIndex: 'address',
      },
      {
        title: '打卡方式',
        dataIndex: 'checkInMethod',
        render: (res) => <div>{res == 1 ? '距离打卡' : '扫码打卡'}</div>,
      },
      {
        title: '打卡距离（米）',
        dataIndex: 'checkInDistance',
      },
      {
        title: '打卡类型',
        dataIndex: 'checkInTypeName',
      },
      {
        title: '场地管理员',
        dataIndex: 'isVerifieds',
        render: (res, record) => (
          <Popover
            content={<div>{this.state.adminListf.map((xz) => xz.nickname).join('、')}</div>}
            title="查看管理员"
            trigger="click"
          >
            <div className="clickFont" onClick={() => this.findAdmin(record.id)}>
              查看
            </div>
          </Popover>
        ),
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
                checked={!text}
                onChange={(value) => {
                  this.front(value, record.id);
                }}
              />
            </div>
          );
        },
      },
      {
        title: '排序',
        dataIndex: 'sort',
      },
      {
        title: '备注',
        dataIndex: 'remark',
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
      },
      {
        title: '操作',
        width: 140,
        dataIndex: '',
        render: (text, record) => {
          return (
            <>
              <span
                className="clickFont"
                onClick={() =>
                  this.setState({ modalVisible: true, isAdd: false, id: record.id, info: record })
                }
              >
                编辑
              </span>

              <Popconfirm
                title={
                  <>
                    <div>删除提示</div>
                    <div>
                      <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                      <span style={{ color: '#ccc' }}>确定删除吗？</span>
                    </div>
                  </>
                }
                onConfirm={() => this.deletes([record.id])}
                // onCancel={cancel}
                okText="是"
                cancelText="否"
              >
                <span className="mL15 red">删除</span>
              </Popconfirm>
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
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                {/* <Col className="gutter-row" span={4} style={{ marginRight: 30 }}>
                  <Form.Item label="设备编号" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col> */}
                <Col className="gutter-row" span={4} style={{ marginRight: 30 }}>
                  <Form.Item label="场地名称" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                {/* <Col className="gutter-row" span={4}>
                  <Form.Item label="设备状态" name="searchField2">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>已置顶</Option>
                      <Option value={0}>未置顶</Option>
                    </Select>
                  </Form.Item>
                </Col> */}
                <Col className="gutter-row" span={4}>
                  <Form.Item label="打卡方式" name="searchType">
                    <Select allowClear placeholder="请选择">
                      <Option value={1}>距离打卡</Option>
                      <Option value={0}>扫码打卡</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="启用状态" name="searchIntStatus">
                    <Select allowClear placeholder="请选择">
                      <Option value={0}>开启</Option>
                      <Option value={1}>关闭</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="创建时间" name="time">
                    <RangePicker format="YYYY-MM-DD" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" style={{ textAlign: 'right', flex: '1 0 220px' }}>
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
          <div
            style={{ backgroundColor: '#fff', padding: 20, minHeight: window.innerHeight - 280 }}
          >
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>打卡场地</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button
                    type="primary"
                    onClick={() => this.setState({ modalVisible: true, isAdd: true })}
                  >
                    +新增
                  </Button>
                  <Popconfirm
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
                  </Popconfirm>
                </div>
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
                pageSize: 10,
                total,
                current: pageNum,
              }}
              // scroll={{ x: '100%' }}
            />

            {this.state.modalVisible && (
              <NewVenues
                id={this.state.id}
                isAdd={this.state.isAdd}
                gymTypelist={this.state.gymTypelist}
                info={this.state.info}
                adminList={this.state.adminList}
                getData={this.getData}
                handleCancel={() => this.setState({ modalVisible: false })}
              />
            )}
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(DakaSite);
