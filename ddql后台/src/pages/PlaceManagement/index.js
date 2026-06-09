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
  Menu,
  Dropdown,
  Space,
  Switch,
  Radio,
  Popconfirm,
  Cascader,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import ExportJsonExcel from 'js-export-excel';
import moment from 'moment';
// import NewVenues from './components/NewVenues';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;

class Login extends React.Component {
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
  };

  componentDidMount() {
    this.getData();
    this.loadDizhi();
  }

  loadDizhi = () => {
    if (window.dizhi) {
      this.formatDizhi(window.dizhi);
    } else {
      const c = document.createElement('script');
      c.src = 'https://admin.nctyt.com/dizhi.js';
      c.onload = () => {
        this.formatDizhi(window.dizhi);
      };
      document.body.appendChild(c);
    }
  };

  formatDizhi = (dizhiData) => {
    const bbb = [];
    dizhiData.forEach((res) => {
      if (!bbb.some((resd) => resd.label === res.province)) {
        bbb.push({ label: res.province, value: res.province, children: [] });
      }
    });
    bbb.forEach((resd) => {
      const aa = dizhiData.filter((res) => res.province === resd.label);
      aa.forEach((res) => {
        if (!resd.children.some((child) => child.label === res.market)) {
          resd.children.push({
            label: res.market,
            value: res.market,
            children: []
          });
        }
      });
    });
    bbb.forEach((resd) => {
      resd.children.forEach((child) => {
        const aa = dizhiData.filter((res) => res.market === child.label);
        aa.forEach((res) => {
          if (!child.children.some((c) => c.label === res.distinguish)) {
            child.children.push({
              label: res.distinguish,
              value: res.distinguish,
            });
          }
        });
      });
    });
    this.setState({ regionOptions: bbb });
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
            name: this.state.name,
            type: this.state.type,
            verificationStatus: this.state.verificationStatus,
            status: this.state.status,
            createBeginTime: this.state.start_time,
            createEndTime: this.state.end_time,
            status: this.state.status,
            region: this.state.region,
            deleteBeginTime: this.state.deleteBeginTime,
            deleteEndTime: this.state.deleteEndTime,
            // order_no: this.state.order_no, //订单编号

            // created_at: this.state.created_at, //创建时间
            pageNum: this.state.pageNum,
            pageSize: 10,
          },
          url: `/ddql/team/select`,
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
              message.error(res.message);
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
        name: vas.name,
        type: vas.type,
        verificationStatus: vas.verificationStatus,
        status: vas.status,
        region: vas.region ? vas.region.join('') : undefined,
        start_time: vas.time ? vas.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        end_time: vas.time ? vas.time[1].format('YYYY-MM-DD 23:59:59') : undefined,
        deleteBeginTime: vas.endtime ? vas.endtime[0].format('YYYY-MM-DD 00:00:00') : undefined,
        deleteEndTime: vas.endtime ? vas.endtime[1].format('YYYY-MM-DD 23:59:59') : undefined,
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
        name: undefined,
        type: undefined,
        verificationStatus: undefined,
        status: undefined,
        start_time: undefined,
        end_time: undefined,
        status: undefined,
        region: undefined,
        deleteBeginTime: undefined,
        deleteEndTime: undefined,
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
  front = (checked, values, name) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        changeIds: [values.id],
        status: checked ? 0 : 1,
      },
      url: '/ddql/team/status/set',
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
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
        if (res && res.code === 10000) {
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
          id: ids,
        },
        url: `/ddql/team/delete`,
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

  export = (e) => {
    const that = this;
    let xhr = new XMLHttpRequest();
    let fileName = `所有团体${moment().format('YYYYMMDD')}.xls`; // 文件名称
    xhr.open('POST', `/ddql/team/export`, true);
    xhr.responseType = 'blob';
    xhr.setRequestHeader('Content-Type', 'application/json');
    const data = {
      name: this.state.name,
      type: this.state.type,
      isVerified: this.state.isVerified,
      status: this.state.status,
      createBeginTime: this.state.start_time,
      createEndTime: this.state.end_time,
      status: this.state.status,
      region: this.state.region,
      deleteBeginTime: this.state.deleteBeginTime,
      deleteEndTime: this.state.deleteEndTime,
    };
    if (!e) {
      data.teamIds = this.state.selectedRowKeys;
    }
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
        title: '团体名称',
        dataIndex: 'name',
        render: (text, record) => {
          return <div>{text}</div>;
        },
      },
      {
        title: '团体类型',
        dataIndex: 'type',
        render: (res, record) => (
          <div>
            {res == 0 ? '企事单位' : res == 1 ? '政府部门' : res == 2 ? '家庭' : '朋友运动群'}
          </div>
        ),
      },
      {
        title: '团体人数',
        dataIndex: 'peopleNumber',
      },
      {
        title: '资质认证',
        dataIndex: 'verificationStatus',
        render: (res, record) => (
          <div>
            {(record.type == 0 || record.type == 1) && (
              <div
                style={{
                  marginLeft: 6,
                  fontSize: 14,
                  color:
                    record.verificationStatus == undefined || record.verificationStatus == 0
                      ? 'rgba(245, 154, 35, 0.84)'
                      : record.verificationStatus == 1
                        ? 'rgba(17, 175, 33, 0.847)'
                        : 'rgba(217, 0, 27, 0.84)',
                }}
              >
                {record.verificationStatus == undefined
                  ? '未认证'
                  : record.verificationStatus == 0
                    ? '审核中'
                    : record.verificationStatus == 1
                      ? '已通过'
                      : '已驳回'}
              </div>
            )}
          </div>
        ),
      },
      {
        title: '地区',
        dataIndex: 'deskDisplay',
        render: (res, record) => (
          <div>{`${record.region}${record.address ? record.address : ''}`}</div>
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
                  this.front(value, record);
                }}
              />
            </div>
          );
        },
      },

      {
        title: '创建时间',
        dataIndex: 'createTime',
      },

      {
        title: '解散时间',
        dataIndex: 'deleteTime',
      },
      {
        title: '操作',
        dataIndex: '',
        render: (text, record) => {
          return (
            <>
              <Link
                to={{
                  pathname: 'placeInformation',
                  search: `?id=${record.id}&edit=1&name=${record.name}`,
                  state: { info: record }
                }}
              >
                编辑
              </Link>

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
                onConfirm={() => this.deletes(record.id)}
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
    const menu = (
      <Menu>
        <Menu.Item key="1" onClick={() => this.export('all')}>
          <a>全部</a>
        </Menu.Item>
        <Menu.Item key="2" onClick={() => this.export()}>
          <a>选中</a>
        </Menu.Item>
      </Menu>
    );
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
                <Col className="gutter-row" span={4} style={{ marginRight: 30 }}>
                  <Form.Item label="团体名称" name="name">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>

                <Col className="gutter-row" span={5} style={{ marginRight: 30 }}>
                  <Form.Item label="团体类型" name="type">
                    <Select allowClear placeholder="请选择">
                      <Option value={0}>企事单位</Option>
                      <Option value={1}>政府部门</Option>
                      <Option value={2}>家庭</Option>
                      <Option value={3}>朋友运动群</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5} style={{ marginRight: 30 }}>
                  <Form.Item label="资质认证" name="verificationStatus">
                    <Select allowClear placeholder="请选择">
                      <Option value={0}>审核中</Option>
                      <Option value={1}>已通过</Option>
                      <Option value={2}>已驳回</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="启用状态" name="status">
                    <Select allowClear placeholder="请选择">
                      <Option value={0}>开启</Option>
                      <Option value={1}>关闭</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="地区" name="region">
                    <Cascader options={this.state.regionOptions || []} placeholder="支持输入搜索" showSearch />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="创建时间" name="time">
                    <RangePicker format="YYYY-MM-DD" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="解散时间" name="endtime">
                    <RangePicker format="YYYY-MM-DD" />
                  </Form.Item>
                </Col>
                <Col
                  className="gutter-row"
                  span={18}
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
            <Row>
              <Col span={6}>
                {' '}
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>团体管理</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Link to={`placeInformation?edit=${0}`}>
                    <Button type="primary">+ 新增团体</Button>
                  </Link>
                  <Dropdown
                    overlay={menu}
                    placement="bottomLeft"
                    arrow={{ pointAtCenter: true }}
                    className="mL15"
                  >
                    {/* <Button>导出</Button> */}
                    <Button className="mL15" loading={this.state.loading}>
                      导出
                    </Button>
                  </Dropdown>
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

            <Modal
              title="关闭前台取号原因"
              open={this.state.isModalOpen}
              onOk={this.handleOk}
              onCancel={this.handleCancel}
            >
              <Row style={{ display: 'flex', alignItems: 'center' }}>
                <Col span={6} style={{ textAlign: 'left', marginRight: 5 }}>
                  关闭前台取号原因:
                </Col>
                <Col span={17}>
                  <Input
                    placeholder="请输入"
                    value={this.state.customReason}
                    onChange={this.CustomReason}
                  />
                </Col>
              </Row>
            </Modal>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(Login);
