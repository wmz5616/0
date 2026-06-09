import React from 'react';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Select,
  Input,
  Table,
  Row,
  Col,
  Form,
  message,
  Popconfirm,
  Spin,
  Alert,
  Modal,
  Upload,
  Switch,
  DatePicker,
  InputNumber,
  Image,
} from 'antd';
import { getToken } from '@/utils/authority';
import { UploadOutlined, PlusOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
// 将connect导入
import { connect, history } from 'umi';
const { RangePicker } = DatePicker;
// 报名管理
const { Option } = Select;
const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 10 },
};
const layouts = {
  labelCol: { span: 5 },
  wrapperCol: { span: 10 },
};
class Shangjia extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();

  state = {
    spinning: false,
    loading: false,
    list: [],
    pageNum: 1,
    selectedRowKeys: [],
    circleList: [],
    qrcodeVisible: false,
    xiaofeiVisible: false,
    shopData: {},
    qrData: {},
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
        searchIntStatus: 1,
      },
      url: `/ddql/common/business/circle/lists`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            circleList: res.data,
          });
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
            searchId: this.state.searchId,
            searchField1: this.state.searchField1,
            keyword: this.state.keyword,
            startTime: this.state.startTime,
            searchField2: this.state.searchField2,
            searchField: this.state.searchField,
            searchIntStatus: this.state.searchIntStatus,
            searchField3: this.state.searchField3,
            endTime: this.state.endTime,
            pageNum: this.state.pageNum,
            pageSize: 10,
          },
          url: `/ddql/business/shop/lists`,
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

  //查询
  onFinish = (vas) => {
    this.setState(
      {
        searchId: vas.searchId,
        searchIntStatus: vas.searchIntStatus,
        searchField1: vas.searchField1,
        keyword: vas.keyword,
        searchField2: vas.searchField2,
        searchField3: vas.searchField3,
        searchField: vas.searchField,
        startTime: vas.time ? vas.time[0].format('YYYY-MM-DD 00:00:00') : undefined,
        endTime: vas.time ? vas.time[1].format('YYYY-MM-DD 23:59:59') : undefined,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  changeStatus = (e) => { };

  //重置
  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        pageNum: 1,
        searchId: undefined,
        searchField1: undefined,
        keyword: undefined,
        startTime: undefined,
        endTime: undefined,
        searchField: undefined,
        searchField2: undefined,
        searchIntStatus: undefined,
        searchField3: undefined,
        selectedRowKeys: [],
        selectedRecord: [],
      },
      () => {
        this.getData();
      },
    );
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
        url: `/ddql/business/shop/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code == 10000) {
            message.success(res.msg);
            this.getData();
          } else {
            message.error(res.msg);
          }
        },
      });
    }
  };

  // 开关场所前台展示
  front = (checked, id, name) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: {
        status: checked ? 1 : 0,
        changeId: id,
      },
      method: 'POST',
      url: `/ddql/business/shop/status`,
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

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    this.setState({ selectedRowKeys });
  };

  showQrcode = (id) => {
    const hide = message.loading('加载中...', 0);
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: id,
      },
      url: `/ddql/business/shop/generateQrCode`,
      method: 'POST',
      myData: (res) => {
        hide();
        if (res && res.code === 10000) {
          const data = res.data || {};
          if (!data?.shopId) {
            message.info('暂无二维码数据！')
          }
          message.success(res.msg);
          this.setState({
            qrcodeVisible: true,
            qrData: data,
          });
        } else {
          message.error(res.msg);
        }
      }
    })
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

  xiaofeiSubmit = () => {
    this.formRefs.current.validateFields().then((values) => {
      const { dispatch } = this.props;
      dispatch({
        type: 'myModel/getSetData',
        payload: {
          searchId: this.state.shopData.id,
          searchType: 1,
          startTime: values.startTime.format('YYYY-MM-DD 00:00:00'),
          endTime: values.endTime.format('YYYY-MM-DD 23:59:59'),
        },
        method: 'POST',
        url: `/ddql/business/shop/topConsumption/status`,
        myData: (res) => {
          if (res.code === 10000) {
            message.success(res.msg);
            this.setState({ xiaofeiVisible: false, shopData: {} });
            this.getData();
          } else {
            message.error(res.msg);
          }
        },
      });
    });
  };

  downloadQrcode = () => {
    this.setState({
      loading: true,
    });
    let xhr = new XMLHttpRequest();
    let fileName = `二维码.png`; // 文件名称
    const that = this;
    xhr.open('POST', `/ddql/business/shop/downloadQrCode`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.responseType = 'blob';
    xhr.setRequestHeader('token', getToken()); // 请求头中的验证信息等（如果有）

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
      that.setState({
        loading: false,
      });
    };
    var data = { searchId: this.state.qrData.shopId };
    xhr.send(JSON.stringify(data)); // 发送请求体（如果有）
  };

  render() {
    const { list = [], total, selectedRowKeys } = this.state;

    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };

    const columns = [
      {
        fixed: 'left',
        title: '商家名称',
        dataIndex: 'name',
      },
      {
        title: '图片',
        dataIndex: 'coverImageUrl',
        render: (text, record) => {
          return (
            <>
              <img src={text} alt="" style={{ width: 103.5, height: 37.5, objectFit: 'contain' }} />
            </>
          );
        },
      },
      {
        title: '所属商圈',
        render: (res, record) => (
          <div>{record.circleNameList && record.circleNameList.map((ax) => ax).join('、')}</div>
        ),
      },
      {
        title: '置顶推荐',
        dataIndex: 'topRecommend',
        render: (res) => (
          <div style={{ color: res == 1 ? 'rgba(3, 191, 22, 0.99)' : 'rgba(245, 154, 35, 0.99)' }}>
            {res == 1 ? '已置顶' : '未置顶'}
          </div>
        ),
      },
      {
        title: '推荐顺序',
        dataIndex: 'recommendOrder',
      },
      {
        title: '置顶时长',
        dataIndex: 'isVerifieds',
        render: (res, record) => (
          <div>{record.topStartTime ? `${record.topStartTime}-${record.topEndTime}` : ''}</div>
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
                checked={text}
                onChange={(value) => {
                  this.front(value, record.id);
                }}
              />
            </div>
          );
        },
      },
      {
        title: '消费置顶',
        dataIndex: 'topConsumption',
        render: (res, record) => (
          <Switch
            checkedChildren="开"
            unCheckedChildren="关"
            checked={res == 1 ? true : false}
            onChange={(checked) => {
              if (checked) {
                this.setState(
                  {
                    xiaofeiVisible: checked ? true : false,
                    shopData: record,
                  },
                  () => {
                    this.formRefs.current.setFieldsValue({
                      startTime: record.topConsumptionStartTime
                        ? dayjs(record.topConsumptionStartTime)
                        : undefined,
                      endTime: record.topConsumptionEndTime
                        ? dayjs(record.topConsumptionEndTime)
                        : undefined,
                    });
                  },
                );
              } else {
                this.props.dispatch({
                  type: 'myModel/getSetData',
                  payload: {
                    searchId: record.id,
                    searchType: 0,
                  },
                  method: 'POST',
                  url: `/ddql/business/shop/topConsumption/status`,
                  myData: (res) => {
                    if (res.code === 10000) {
                      message.success(res.msg);
                      this.getData();
                    } else {
                      message.error(res.msg);
                    }
                  },
                });
              }
            }}
          />
        ),
      },
      {
        title: '收款二维吗',
        render: (text, record) => <a onClick={() => this.showQrcode(record.id)}>二维码</a>,
      },
      {
        title: '资质认证',
        dataIndex: 'qualificationCert',
        render: (res) => (
          <div
            style={{
              color: res == 0 ? '#189eff' : res == 1 ? '#f59a23' : res == 2 ? '#27b43e' : '#da1c30',
            }}
          >
            {res == 0 ? '未认证' : res == 1 ? '待审核' : res == 2 ? '已通过' : '已驳回'}
          </div>
        ),
      },
      {
        title: '费率',
        dataIndex: 'rate',
      },
      {
        title: '点击次数',
        dataIndex: 'clickCount',
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
        title: '商家状态',
        fixed: 'right',
        dataIndex: 'shopStatus',
        render: (res) => (
          <div style={{ color: res == 0 ? '#27b43e' : res == 2 ? '#da1c30' : '#f59a23' }}>
            {res == 0 ? '正常' : res == 2 ? '已注销' : '禁用'}
          </div>
        ),
      },
      {
        title: '操作',
        fixed: 'right',
        render: (text, record) => {
          return (
            <div>
              <span
                className="clickFont"
                onClick={() => {
                  history.push({
                    pathname: '/shangjiaManagement/shangjiaInfomation',
                    state: {
                      type: 'edit',
                      shopName: record.shopName,
                      id: record.id,
                    },
                  });
                }}
              >
                编辑
              </span>
              {record.shopStatus == 2 && (
                <Popconfirm
                  title={
                    <>
                      <div>注销提示</div>
                      <div>
                        <span style={{ color: 'red' }}>确定要取消注销商家吗？</span>
                      </div>
                    </>
                  }
                  onConfirm={() => { }}
                  okText="是"
                  cancelText="否"
                >
                  <span className="mL15 red">删除</span>
                </Popconfirm>
              )}
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
            </div>
          );
        },
      },
    ];

    return (
      <Spin spinning={this.state.spinning}>
        <PageContainer
          header={{
            title: ``,
          }}
        >
          <div style={{ backgroundColor: '#fff', padding: '20px 20px 0 20px', marginBottom: 15 }}>
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="商家名称" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="所属商圈" name="searchId">
                    <Select showSearch placeholder="请选择" optionFilterProp="children">
                      {this.state.circleList.map((sa) => (
                        <Option value={sa.id}>{sa.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="置顶推荐" name="searchField1">
                    <Select showSearch placeholder="请选择">
                      <Option value={1}>已置顶</Option>
                      <Option value={0}>未置顶</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="启用状态" name="searchIntStatus">
                    <Select showSearch placeholder="请选择">
                      <Option value={1}>是</Option>
                      <Option value={0}>否</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={6}>
                  <Form.Item label="创建时间" name="time">
                    <RangePicker format="YYYY-MM-DD" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="收款配置" name="searchField2">
                    <Select showSearch placeholder="请选择">
                      <Option value={1}>开启</Option>
                      <Option value={0}>关闭</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="商家状态" name="searchField">
                    <Select showSearch placeholder="请选择">
                      <Option value={0}>正常</Option>
                      <Option value={1}>禁用</Option>
                      <Option value={2}>已注销</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="资质认证" name="searchField3">
                    <Select showSearch placeholder="请选择">
                      <Option value={0}>待审核</Option>
                      <Option value={1}>已通过</Option>
                      <Option value={2}>已驳回</Option>
                    </Select>
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
              <Col span={12}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>商家信息管理</h1>
              </Col>
              <Col span={12} style={{ textAlign: 'right' }}>
                <Button
                  type="primary"
                  onClick={() => {
                    console.log(1);
                    history.push({
                      pathname: '/shangjiaManagement/shangjiaInfomation',
                      state: {
                        type: 'add',
                      },
                    });
                  }}
                  style={{ marginRight: 15 }}
                >
                  +新增商家
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
                  <Button danger>删除</Button>
                </Popconfirm>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 15 }}
              columns={columns}
              className="csdivcenter"
              rowKey="id"
              rowSelection={rowSelection}
              dataSource={list}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChange,
                pageSize: 10,
                total,
                current: this.state.pageNum,
              }}
              scroll={{ x: 'max-content' }}
            />
          </div>
          <Modal
            title="消费置顶"
            open={this.state.xiaofeiVisible}
            onOk={this.xiaofeiSubmit}
            onCancel={(e) => {
              this.formRefs.current.resetFields();
              this.setState({ xiaofeiVisible: false, shopData: {} });
            }}
            maskClosable={false}
          >
            <Form ref={this.formRefs} {...layout}>
              <Form.Item label="商家" rules={[{ required: false }]}>
                {this.state.shopData?.name || ''}
              </Form.Item>
              <Form.Item label="消费置顶开始时间" name="startTime" rules={[{ required: true }]}>
                <DatePicker format="YYYY-MM-DD" />
              </Form.Item>
              <Form.Item label="消费置顶结束时间" name="endTime" rules={[{ required: true }]}>
                <DatePicker format="YYYY-MM-DD" />
              </Form.Item>
            </Form>
          </Modal>
          <Modal
            title="收款二维码"
            open={this.state.qrcodeVisible}
            onOk={(e) => this.setState({ qrcodeVisible: false })}
            onCancel={(e) => this.setState({ qrcodeVisible: false })}
          >
            <Form {...layouts}>
              <Form.Item label="二维码预览">
                <Image
                  width={200}
                  height={200}
                  src={this.state.qrData?.qrCode || ''}
                  style={{ background: '#fff' }}
                />
              </Form.Item>
              <Form.Item name="files" label="下载二维码">
                <Button onClick={this.downloadQrcode} icon={<UploadOutlined />} loading={this.state.loading}>
                  下载文件
                </Button>
              </Form.Item>
            </Form>
          </Modal>
        </PageContainer>
      </Spin>
    );
  }
}

export default connect((allModels) => ({}))(Shangjia);
