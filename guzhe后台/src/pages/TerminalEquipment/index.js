import { getToken } from '@/utils/authority';
import { urlName } from '@/utils/utils';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Radio,
  Row,
  Select,
  Spin,
  Switch,
  Table,
  Upload,
} from 'antd';
import React from 'react';
// 将connect导入
import { post } from '@/utils/request';
import CKEditor from 'react-ckeditor-wrapper';
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
class Index extends React.Component {
  formRef = React.createRef();
  formRefss = React.createRef();
  formRefsss = React.createRef();

  state = {
    spinning: false,
    loading: false,
    lloading: false,
    list: [],
    pageNum: 1,
    total: 0,
    logPageNum: 1,
    logTotal: 0,
    selectedRowKeys: [],
    circleList: [],
    shopData: {},
    typeList: [],
    showModal: false,
    content: '',
    showLog: false,
    logList: [],
    deviceData: {},
    isEdit: false,
    showEquipment: false,
  };

  componentDidMount() {
    this.getData();
    this.getAdminList();
  }

  getAdminList = async () => {
    const res = await post(`/guzhe/common/supermarket/lists`, {
      searchIntStatus: 1,
    });
    if (res && res.code == 10000) {
      this.setState({
        circleList: res.data || [],
      });
    } else {
      message.error(res?.msg);
    }
  };

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      async () => {
        //列表
        const res = await post(`/guzhe/equipment/select`, {
          searchStrField1: this.state.searchStrField1,
          searchIntStatus: this.state.searchField1,
          keyword: this.state.keyword,
          searchField1: this.state.searchIntStatus,
          startTime: this.state.startTime,
          endTime: this.state.endTime,
          pageNum: this.state.pageNum,
          pageSize: 10,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          this.setState({
            list: res.data?.list || [],
            total: res.data.total,
          });
        } else {
          message.error(res?.msg);
        }
      },
    );
  };

  onOk = () => {
    this.formRefsss.current.validateFields().then(async (values) => {
      const res = await post(
        this.state.isEdit ? '/guzhe/equipment/update' : `/guzhe/equipment/add`,
        {
          serialNumber: values.serialNumber,
          supermarketId: values.supermarketId,
          contactPhone: values.contactPhone,
          money: values.money ? values.money * 100 : undefined,
          status: values.status ? 1 : 0,
          sort: values.sort,
          remark: values.remark,
          id: this.state.isEdit ? this.state.editId : undefined,
        },
      );
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.setState({
          showEquipment: false,
        });
        this.getData();
      } else {
        message.error(res?.msg);
      }
    });
  };

  onCancel = () => {
    this.setState({
      showEquipment: false,
    });
    this.formRefsss.current.resetFields();
  };

  getDatas = async () => {
    //列表
    this.setState({
      lloading: true,
    });
    const res = await post(`/guzhe/equipment/log/lists`, {
      searchId: this.state.deviceData?.id,
      searchIntStatus: this.state.searchField1s,
      startTime: this.state.startTimes,
      endTime: this.state.endTimes,
      pageNum: this.state.logPageNum,
      pageSize: 10,
    });
    this.setState({
      lloading: false,
    });
    if (res && res.code == 10000) {
      this.setState({
        logList: res.data.list,
        logTotal: res.data.total,
      });
    } else {
      message.error(res?.msg);
    }
  };

  //查询
  onFinish = (vas) => {
    this.setState(
      {
        searchStrField1: vas.searchStrField1,
        searchIntStatus: vas.searchIntStatus,
        searchField1: vas.searchField1,
        keyword: vas.keyword,
        startTime: vas.time
          ? vas.time[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        endTime: vas.time
          ? vas.time[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  //查询
  onFinishs = (vas) => {
    this.setState(
      {
        searchField1s: vas.searchField1s,
        startTimes: vas.time
          ? vas.time[0].format('YYYY-MM-DD 00:00:00')
          : undefined,
        endTimes: vas.time
          ? vas.time[1].format('YYYY-MM-DD 23:59:59')
          : undefined,
        logPageNum: 1,
      },
      () => {
        this.getDatas();
      },
    );
  };

  changeStatus = (e) => {};

  //重置
  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        pageNum: 1,
        searchStrField1: undefined,
        searchField1: undefined,
        keyword: undefined,
        startTime: undefined,
        endTime: undefined,
        searchIntStatus: undefined,
        selectedRowKeys: [],
      },
      () => {
        this.getData();
      },
    );
  };

  //重置
  resetss = (vas) => {
    this.formRefss.current.resetFields();
    this.setState(
      {
        logPageNum: 1,
        searchField1s: undefined,
        startTimes: undefined,
        endTimes: undefined,
      },
      () => {
        this.getDatas();
      },
    );
  };

  //删除
  deletes = async (ids) => {
    if (ids.length == 0) {
      message.info('请选择需要删除的数据');
    } else {
      const res = await post(`/guzhe/equipment/delete`, {
        deleteIds: ids,
      });
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    }
  };

  // 开关场所前台展示
  front = async (checked, id, name) => {
    const res = await post(`/guzhe/equipment/status/set`, {
      status: checked ? 1 : 0,
      changeIds: [id],
    });
    if (res.code == 10000) {
      message.success(res.msg);
      this.getData();
    } else {
      message.error(res?.msg);
    }
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

  pageChanges = (page) => {
    //列表改变页码
    this.setState(
      {
        logPageNum: page,
      },
      () => {
        this.getDatas();
      },
    );
  };

  handleCancel = () => {
    this.setState({
      content: '',
      isShowContract: 0,
      contractConfig: {},
      showModal: false,
    });
  };

  handleOk = async () => {
    const res = await post(`/guzhe/system/basic/config/rental/update`, {
      configData: Object.entries(this.state.contractConfig).map(
        ([key, value]) => {
          if (key === 'show_rental_contract') {
            value = this.state.isShowContract;
          } else if (key === 'rental_contract') {
            value = this.state.content;
          }
          return { key, value };
        },
      ),
    });
    if (res && res.code == 10000) {
      message.success(res.msg);
      this.handleCancel();
    } else {
      message.error(res?.msg);
    }
  };

  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };

  getScreenContract = async () => {
    const res = await post(`/guzhe/system/basic/config/rental`);
    if (res && res.code == 10000) {
      const data = res.data.reduce((pre, item) => {
        pre[item.key] = item.value;
        return pre;
      }, {});
      this.setState({
        contractConfig: data,
        content: data['rental_contract'] || '',
        isShowContract: +data['show_rental_contract'] || 0,
        showModal: true,
      });
    } else {
      message.error(res?.msg);
    }
  };

  render() {
    const { list = [], total, selectedRowKeys } = this.state;

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',
        headers: { token: localStorage.getItem('token') },
        onChange: (info) => {
          const fileType = [
            'doc',
            'txt',
            'pdf',
            'zip',
            'rar',
            'xls',
            'xlsx',
            'docs',
            'pptx',
            'ppt',
          ];
          if (info.file.status == 'done') {
            console.log(info.file);
            const url = urlName + info.file.response.data.url;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', url);

            ckeditor.instance.insertElement(ele);
            // }
          }
        },
      };
    };

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
        title: '设备编号',
        dataIndex: 'serialNumber',
      },
      {
        title: '设备状态',
        dataIndex: 'onlineStatus',
        render: (res) => {
          return (
            <div style={{ color: res == 0 ? '#5cd668' : '#e65a6b' }}>
              {res == 0 ? '在线' : '离线'}
            </div>
          );
        },
      },
      {
        title: '设备日志',
        render: (res, record) => {
          return (
            <div>
              <a
                onClick={() => {
                  this.setState({ showLog: true, deviceData: record }, () => {
                    this.getDatas();
                  });
                }}
              >
                查看详情
              </a>
            </div>
          );
        },
      },
      {
        title: '屏幕画面',
        render: (res, record) => (
          <div
            className="clickFont"
            onClick={() => {
              if (record.screenshotUrl) {
                window.open(record.screenshotUrl);
              } else {
                message.info('暂无屏幕画面');
              }
            }}
          >
            查看
          </div>
        ),
      },
      {
        title: '所属商超',
        width: 200,
        dataIndex: 'supermarketId',
        render: (res) => (
          <div>
            {this.state.circleList.find((i) => i.id == res)?.name || ''}
          </div>
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
        title: '店位每月租金',
        dataIndex: 'money',
        render: (res) => <div>{res ? res / 100 : undefined}</div>,
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
        title: '备注',
        dataIndex: 'remark',
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
                  this.setState(
                    {
                      editId: record.id,
                      isEdit: true,
                      showEquipment: true,
                    },
                    () => {
                      this.formRefsss.current.setFieldsValue({
                        serialNumber: record.serialNumber,
                        supermarketId: record.supermarketId,
                        contactPhone: record.contactPhone,
                        money: record.money / 100,
                        status: record.status,
                        sort: record.sort,
                        remark: record.remark,
                      });
                    },
                  );
                }}
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
            </div>
          );
        },
      },
    ];

    const columnss = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '设备状态',
        dataIndex: 'status',
        render: (res) => {
          return (
            <div style={{ color: res == 1 ? '#5cd668' : '#e65a6b' }}>
              {res == 1 ? '在线' : '离线'}
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
      <Spin spinning={this.state.spinning}>
        <PageContainer
          header={{
            title: ``,
          }}
        >
          <div
            style={{
              backgroundColor: '#fff',
              padding: '20px 20px 0 20px',
              marginBottom: 15,
            }}
          >
            <Form ref={this.formRef} onFinish={this.onFinish}>
              <Row gutter={16}>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="设备编号" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="所属商超" name="searchStrField1">
                    <Select
                      showSearch
                      placeholder="请选择"
                      optionFilterProp="children"
                    >
                      {this.state.circleList.map((sa) => (
                        <Option value={sa.name}>{sa.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="设备状态" name="searchField1">
                    <Select showSearch placeholder="请选择">
                      <Option value={0}>在线</Option>
                      <Option value={1}>离线</Option>
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

          <div
            style={{
              backgroundColor: '#fff',
              padding: 20,
              minHeight: window.innerHeight - 280,
            }}
          >
            <Row>
              <Col span={12}>
                <h1 style={{ fontWeight: '600', fontSize: '18px' }}>
                  终端设备管理
                </h1>
              </Col>
              <Col span={12} style={{ textAlign: 'right' }}>
                <Button
                  type="primary"
                  onClick={() =>
                    this.setState(
                      {
                        showEquipment: true,
                        isEdit: false,
                      },
                      () => {
                        this.formRefsss.current.resetFields();
                      },
                    )
                  }
                  style={{ marginRight: 15 }}
                >
                  +新增设备
                </Button>
                <Button
                  style={{ marginRight: 15 }}
                  variant="outlined"
                  color="blue"
                  onClick={this.getScreenContract}
                >
                  屏幕店租用合约
                </Button>
                <Popconfirm
                  title={
                    <>
                      <div>删除提示</div>
                      <div>
                        <span style={{ color: 'red' }}>删除的内容不可恢复</span>
                        ，<span style={{ color: '#ccc' }}>确定删除吗？</span>
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
            title="屏幕店租用合约"
            width="45%"
            visible={this.state.showModal}
            onOk={this.handleOk}
            onCancel={this.handleCancel}
            destroyOnClose
          >
            <Form>
              <Form.Item
                label={
                  <div>
                    <span style={{ color: 'red' }}>*</span>
                    是否展示屏幕店租用合约
                  </div>
                }
              >
                <Radio.Group
                  value={this.state.isShowContract}
                  onChange={(e) =>
                    this.setState({ isShowContract: e.target.value })
                  }
                >
                  <Radio value={0}>否</Radio>
                  <Radio value={1}>是</Radio>
                </Radio.Group>
              </Form.Item>
              <Form.Item
                label={
                  <span>
                    {this.state.isShowContract == 1 && (
                      <span style={{ color: 'red' }}>*</span>
                    )}
                    屏幕店租用合约
                  </span>
                }
                rules={[{ required: true, message: '请输入!' }]}
              >
                <div style={{ position: 'relative' }}>
                  <Upload
                    showUploadList={false}
                    accept={'image/*'}
                    headers={{
                      token: getToken(),
                    }}
                    {...uploadProps(1)}
                  >
                    <div className="zxc" />
                  </Upload>
                  <CKEditor
                    ref={(ckeditor) => {
                      this.ckeditor = ckeditor;
                    }}
                    value={this.state.content}
                    config={{
                      toolbar: [
                        {
                          name: 'clipboard',
                          items: [
                            'Cut',
                            'Copy',
                            'Paste',
                            'PasteText',
                            'PasteFromWord',
                            '-',
                          ],
                        },
                        {
                          name: 'basicstyles',
                          items: [
                            'Bold',
                            'Italic',
                            'Underline',
                            '-',
                            'CopyFormatting',
                          ],
                        },
                        {
                          name: 'paragraph',
                          items: [
                            'NumberedList',
                            'BulletedList',
                            '-',
                            'Outdent',
                            'Indent',
                            '-',
                            'JustifyLeft',
                            'JustifyCenter',
                            'JustifyRight',
                            'JustifyBlock',
                            '-',
                          ],
                        },
                        { name: 'links', items: ['Link', 'Unlink'] },
                        { name: 'insert', items: ['Image', 'Table'] },
                        { name: 'styles', items: ['Font', 'FontSize'] },
                        { name: 'colors', items: ['TextColor', 'BGColor'] },
                        { name: 'tools', items: ['Maximize'] },
                      ],
                      extraPlugins: 'placeholder',
                      height: 250,
                      // uploadUrl: '/home/media/upload',
                      removeDialogTabs: 'image:advanced;link:advanced',
                    }}
                    onChange={this.updateContent}
                  />
                </div>
              </Form.Item>
            </Form>
          </Modal>
          <Modal
            title={`设备日志-${this.state.deviceData.serialNumber || ''}`}
            width="45%"
            visible={this.state.showLog}
            cancelText=""
            onOk={() => {
              this.setState({ showLog: false });
            }}
            onCancel={() => {
              this.setState({ showLog: false });
            }}
          >
            <Form ref={this.formRefss} onFinish={this.onFinishs}>
              <Row gutter={16}>
                <Col className="gutter-row" span={8}>
                  <Form.Item label="设备状态" name="searchField1s">
                    <Select showSearch placeholder="请选择">
                      <Option value={1}>在线</Option>
                      <Option value={2}>离线</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={10}>
                  <Form.Item label="更新时间" name="time">
                    <RangePicker format="YYYY-MM-DD" />
                  </Form.Item>
                </Col>
                <Col
                  className="gutter-row"
                  span={6}
                  style={{ textAlign: 'right', flex: '1 0 220px' }}
                >
                  <Form.Item>
                    <Button type="primary" htmlType="submit">
                      查询
                    </Button>

                    <Button className="mL15" onClick={this.resetss}>
                      重置
                    </Button>
                  </Form.Item>
                </Col>
              </Row>
            </Form>
            <Table
              style={{ marginTop: 15 }}
              columns={columnss}
              className="csdivcenter"
              rowKey="id"
              dataSource={this.state.logList}
              loading={this.state.lloading}
              pagination={{
                showSizeChanger: false,
                onChange: this.pageChanges,
                pageSize: 10,
                total: this.state.logTotal,
                current: this.state.logPageNum,
              }}
              scroll={{ x: 'max-content' }}
            />
          </Modal>
          <Modal
            title={`${this.state.isEdit ? '编辑' : '新增'}设备`}
            width="35%"
            visible={this.state.showEquipment}
            onOk={this.onOk}
            onCancel={this.onCancel}
          >
            <Form ref={this.formRefsss}>
              <Row gutter={16}>
                <Col className="gutter-row" span={24}>
                  <Form.Item
                    label="设备编号"
                    name="serialNumber"
                    rules={[{ required: true, message: '请输入!' }]}
                  >
                    <Input placeholder="请输入"></Input>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={24}>
                  <Form.Item
                    label="所属商超"
                    name="supermarketId"
                    rules={[{ required: true, message: '请选择!' }]}
                  >
                    <Select
                      showSearch
                      placeholder="请选择"
                      optionFilterProp="children"
                    >
                      {this.state.circleList.map((sa) => (
                        <Option key={sa.id} value={sa.id}>
                          {sa.name}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={24}>
                  <Form.Item label={<div>客服电话</div>}>
                    <Form.Item name="contactPhone" noStyle>
                      <Input placeholder="请输入"></Input>
                    </Form.Item>
                    <span style={{ color: '#999e9c' }}>
                      展示在设备的客服电话
                    </span>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={24}>
                  <Form.Item
                    label={
                      <div>
                        <span style={{ color: 'red' }}>*</span>店位每月租金
                      </div>
                    }
                  >
                    <Form.Item name="money" noStyle>
                      <InputNumber min={0} placeholder="请输入"></InputNumber>
                    </Form.Item>
                    <span style={{ marginLeft: '5px' }}>元</span>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={24}>
                  <Form.Item
                    label="启用状态"
                    initialValue={true}
                    name="status"
                    rules={[{ required: true, message: '请选择!' }]}
                  >
                    <Switch checkedChildren="启用" unCheckedChildren="禁用" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={24}>
                  <Form.Item label="排序" name="sort" initialValue={0}>
                    <InputNumber min={0} placeholder="请输入"></InputNumber>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={24}>
                  <Form.Item
                    label="备注"
                    name="remark"
                    rules={[{ required: false, message: '请输入' }]}
                  >
                    <Input.TextArea
                      rows={4}
                      placeholder="请输入"
                    ></Input.TextArea>
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Modal>
        </PageContainer>
      </Spin>
    );
  }
}

export default Index;
