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
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import ExportJsonExcel from 'js-export-excel';
import moment from 'moment';
import HaibaoSettingModal from './components/HaibaoSettingModal';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TextArea } = Input;

class DeviceList extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  formRefsd = React.createRef();
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
    haobaoModalVisible: false,
    listLogData: [],
    pageNums: 1,
  };

  componentDidMount() {
    this.getData();
    this.getAdminList(1);
  }
  getAdminList = (e) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        pageSize: 30,
        pageNum: e,
      },
      url: `/ddql/checkInPlace/select`,
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
            searchIntStatus: this.state.searchIntStatus,
            keyword: this.state.keyword,
            searchField1: this.state.searchField1,
            searchType: this.state.searchType,
            searchStrField1: this.state.searchStrField1,
            startTime: this.state.start_time,
            endTime: this.state.end_time,
            pageNum: this.state.pageNum,
            pageSize: 10,
          },
          url: `/ddql/equipment/select`,
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

  getListLog = (id) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchIntStatus: this.state.searchIntStatusd,
        startTime: this.state.start_timed,
        endTime: this.state.end_timed,
        pageNum: this.state.pageNums,
        pageSize: 10,
        searchId: this.state.id,
      },
      url: `/ddql/equipment/log/lists`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            listLogData: res.data.list,
            totals: res.data.total,
          });
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
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

  onFinishd = (vas) => {
    this.setState(
      {
        searchIntStatusd: vas.searchIntStatus,
        start_timed: vas.time ? vas.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        end_timed: vas.time ? vas.time[1].format('YYYY-MM-DD 23:59:59') : undefined,
        pageNums: 1,
      },
      () => {
        this.getListLog();
      },
    );
  };

  onFinish = (vas) => {
    this.setState(
      {
        searchIntStatus: vas.searchIntStatus,
        keyword: vas.keyword,
        searchField1: vas.searchField1,
        searchType: vas.searchType,
        searchStrField1: vas.searchStrField1,
        start_time: vas.time ? vas.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        end_time: vas.time ? vas.time[1].format('YYYY-MM-DD 23:59:59') : undefined,
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
        searchIntStatus: undefined,
        keyword: undefined,
        searchField1: undefined,
        searchType: undefined,
        searchStrField1: undefined,
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

  resetsd = () => {
    this.formRefsd.current.resetFields();
    this.setState(
      {
        searchIntStatusd: undefined,
        start_timed: undefined,
        end_timed: undefined,
      },
      () => {
        this.getListLog();
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
        changeId: id,
        [name]: checked ? 1 : 0,
      },
      method: 'POST',
      url: `/ddql/shop/setStatus`,
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
        url: `/ddql/equipment/delete`,
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

  handleSubmit = () => {
    this.formRefs.current.validateFields().then((values) => {
      const params = {
        checkInPlaceId: values.checkInPlaceId,
        serialNumber: values.serialNumber,
        enableStatus: values.enableStatus ? 0 : 1,
        contactPhone: values.contactPhone,
        sort: values.sort,
        remark: values.remark,
      };
      if (!this.state.isAdd) {
        params.id = this.state.id;
      }
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        url: this.state.isAdd ? `/ddql/equipment/add` : `/ddql/equipment/update`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.getData();
            this.setState({ modalVisible: false });
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    });
  };

  setStatus = (status, values) => {
    const params = {
      checkInPlaceId: values.checkInPlaceId,
      serialNumber: values.serialNumber,
      enableStatus: status ? 0 : 1,
      contactPhone: values.contactPhone,
      sort: values.sort,
      remark: values.remark,
      id: values.id,
    };
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: `/ddql/equipment/update`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  deviceEdit = (record) => {
    this.setState(
      {
        modalVisible: true,
        isAdd: false,
        id: record.id,
      },
      () => {
        setTimeout(() => {
          this.formRefs.current.setFieldsValue({
            checkInPlaceId: record.checkInPlaceId,
            serialNumber: record.serialNumber,
            enableStatus: record.enableStatus == 1 ? false : true,
            contactPhone: record.contactPhone,
            sort: record.sort,
            remark: record.remark,
          });
        }, 300);
      },
    );
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
        title: '设备编号',
        dataIndex: 'serialNumber',
      },
      {
        title: '设备状态',
        dataIndex: 'onlineStatus',
        render: (text, record) => {
          return (
            <div
              style={{ color: text == 1 ? 'rgba(217, 0, 27, 0.64)' : 'rgba(3, 191, 22, 0.647)' }}
            >
              {text == 1 ? '离线' : '在线'}
            </div>
          );
        },
      },
      {
        title: '设备日志',
        dataIndex: 'isVerifieds',
        render: (res, record) => (
          <div
            className="clickFont"
            onClick={() =>
              this.setState(
                { id: record.id, logModalVisible: true, deviceNo: record.serialNumber },
                () => {
                  this.getListLog();
                },
              )
            }
          >
            查看详情
          </div>
        ),
      },
      {
        title: '关联打卡场地',
        dataIndex: 'checkInPlaceName',
        render: (text, record) => {
          return <div>{text}</div>;
        },
      },
      {
        title: '启用状态',
        dataIndex: 'enableStatus',
        render: (text, record) => {
          return (
            <div>
              <Switch
                checkedChildren="开启"
                unCheckedChildren="关闭"
                checked={text == 0 ? true : false}
                onChange={(value) => this.setStatus(value, record)}
              />
            </div>
          );
        },
      },
      {
        title: '备注',
        dataIndex: 'remark',
      },
      {
        title: '排序',
        dataIndex: 'sort',
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
      },
      {
        title: '操作',
        dataIndex: '',
        render: (text, record) => {
          return (
            <>
              <span
                className="clickFont"
                onClick={() => {
                  this.setState({ haobaoModalVisible: true, id: record.id });
                }}
              >
                海报设置
              </span>
              <span className="clickFont mL15" onClick={() => this.deviceEdit(record)}>
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
    const deviceColumn = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '设备状态',
        dataIndex: 'status',
        render: (text, record) => {
          return (
            <div>
              <div>
                {text == 1 && <span className="luSe">在线</span>}
                {text == 2 && <span className="red">离线</span>}
              </div>
            </div>
          );
        },
      },
      {
        title: '更新时间',
        dataIndex: 'createdTime',
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
                <Col className="gutter-row" span={5}>
                  <Form.Item label="设备编号" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="设备状态" name="searchIntStatus">
                    <Select allowClear placeholder="请选择">
                      <Option value={0}>在线</Option>
                      <Option value={1}>离线</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="场地名称" name="searchStrField1">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={4}>
                  <Form.Item label="启用状态" name="searchField1">
                    <Select allowClear placeholder="请选择">
                      <Option value={0}>启用</Option>
                      <Option value={1}>禁用</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="创建时间" name="time">
                    <RangePicker format="YYYY-MM-DD" style={{ width: '100%' }} />
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
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>设备管理</h1>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  <Button
                    type="primary"
                    onClick={() => this.setState({ modalVisible: true, isAdd: true })}
                  >
                    +新增设备
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
            <Modal
              style={{ minWidth: '50%' }}
              open={this.state.modalVisible}
              title={this.state.isAdd ? '新增设备' : '编辑设备'}
              onCancel={() => this.setState({ modalVisible: false })}
              onOk={this.handleSubmit}
              destroyOnHidden
              // footer={[]}
            >
              <Form
                ref={this.formRefs}
                labelCol={{
                  span: 4,
                }}
                wrapperCol={{
                  span: 16,
                }}
                initialValues={{
                  remember: true,
                }}
                autoComplete="off"
              >
                <Form.Item
                  label="设备编号"
                  name="serialNumber"
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <Input placeholder="请输入" />
                </Form.Item>
                <div
                  style={{ color: '#ccc', paddingLeft: '17%', marginBottom: 10, marginTop: -12 }}
                >
                  设备编号唯一
                </div>
                <Form.Item
                  label="关联打卡场地"
                  name="checkInPlaceId"
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <Select placeholder="请选择">
                    {this.state.adminList
                      .filter((x) => x.checkInMethod == 0)
                      .map((item) => (
                        <Option value={item.id} key={item.id}>
                          {item.name}
                        </Option>
                      ))}
                  </Select>
                </Form.Item>
                <Form.Item
                  label="客服电话"
                  name="contactPhone"
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <Input placeholder="请输入" />
                </Form.Item>
                <Form.Item
                  label="启用状态"
                  name="enableStatus"
                  rules={[{ required: true, message: '请选择!' }]}
                  valuePropName="checked"
                  initialValue={true}
                >
                  <Switch checkedChildren="开启" unCheckedChildren="关闭" />
                </Form.Item>
                <Form.Item
                  label="排序"
                  name="sort"
                  rules={[{ required: true, message: '请选择!' }]}
                >
                  <InputNumber placeholder="请输入" />
                </Form.Item>
                <Form.Item label="备注" name="remark">
                  <TextArea rows={4} placeholder="请输入" />
                </Form.Item>
              </Form>
            </Modal>
            {this.state.haobaoModalVisible && (
              <HaibaoSettingModal
                id={this.state.id}
                cancelModal={() => this.setState({ haobaoModalVisible: false })}
              />
            )}
            <Modal
              visible={this.state.logModalVisible}
              title={`在线日志-${this.state.deviceNo}`}
              style={{ minWidth: '65%' }}
              onCancel={() => this.setState({ logModalVisible: false })}
              footer={[]}
            >
              <Form ref={this.formRefsd} onFinish={this.onFinishd}>
                <Row gutter={16}>
                  <Col className="gutter-row" span={6}>
                    <Form.Item label="设备状态" name="searchIntStatus">
                      <Select allowClear placeholder="请选择">
                        <Option value={1}>在线</Option>
                        <Option value={2}>离线</Option>
                      </Select>
                    </Form.Item>
                  </Col>
                  <Col className="gutter-row" span={8}>
                    <Form.Item label="更新时间" name="time">
                      <RangePicker format="YYYY-MM-DD" style={{ width: '100%' }} />
                    </Form.Item>
                  </Col>

                  <Col className="gutter-row" style={{ textAlign: 'right', flex: '1 0 220px' }}>
                    <Form.Item>
                      <Button type="primary" htmlType="submit">
                        查询
                      </Button>

                      <Button className="mL15" onClick={this.resetsd}>
                        重置
                      </Button>
                    </Form.Item>
                  </Col>
                </Row>
              </Form>
              <Table
                style={{ marginTop: 15 }}
                rowKey="id"
                rowSelection={false}
                columns={deviceColumn}
                dataSource={this.state.listLogData}
                pagination={{
                  showSizeChanger: false,
                  onChange: (e) => this.setState({ pageNums: e }, () => this.getListLog()),
                  pageSize: 10,
                  total: this.state.totals,
                  current: this.state.pageNum,
                }}
                scroll={{ y: 700 }}
              />
            </Modal>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(DeviceList);
