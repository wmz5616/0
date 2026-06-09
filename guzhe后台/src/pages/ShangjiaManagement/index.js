import { getToken } from '@/utils/authority';
import { urlName } from '@/utils/utils';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Input,
  message,
  Modal,
  Popconfirm,
  Radio,
  Row,
  Select,
  Spin,
  Switch,
  Table,
  Tooltip,
  Upload,
} from 'antd';
import React from 'react';
// 将connect导入
import { post } from '@/utils/request';
import { history } from '@umijs/max';
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
class Shangjia extends React.Component {
  formRef = React.createRef();

  state = {
    spinning: false,
    loading: false,
    list: [],
    pageNum: 1,
    selectedRowKeys: [],
    circleList: [],
    shopData: {},
    typeList: [],
    showModal: false,
    content: '',
  };

  componentDidMount() {
    this.getAdminList();
    this.getTypeList()
  }

  getTypeList = async () => {
    const res = await post(`/guzhe/common/industry_category/lists`);
    if (res && res.code == 10000) {
      this.setState({
        typeList: res.data || [],
      },()=>{
        this.getData();
      });
    } else {
      message.error(res?.msg);
    }
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
        const res = await post(`/guzhe/shop/lists`, {
          searchId: this.state.searchId,
          searchField5: this.state.searchField5,
          searchField1: this.state.searchField1,
          keyword: this.state.keyword,
          startTime: this.state.startTime,
          searchField4: this.state.searchField4,
          searchIntStatus: this.state.searchIntStatus,
          searchField3: this.state.searchField3,
          endTime: this.state.endTime,
          pageNum: this.state.pageNum,
          pageSize: 10,
        });
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
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

  //查询
  onFinish = (vas) => {
    this.setState(
      {
        searchId: vas.searchId,
        searchField5: vas.searchField5,
        searchIntStatus: vas.searchIntStatus,
        searchField1: vas.searchField1,
        keyword: vas.keyword,
        searchField3: vas.searchField3,
        searchField4: vas.searchField4,
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

  changeStatus = (e) => { };

  //重置
  resets = (vas) => {
    this.formRef.current.resetFields();
    this.setState(
      {
        pageNum: 1,
        searchId: undefined,
        searchField5: undefined,
        searchField1: undefined,
        keyword: undefined,
        startTime: undefined,
        endTime: undefined,
        searchField4: undefined,
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
  deletes = async (ids) => {
    if (ids.length == 0) {
      message.info('请选择需要删除的数据');
    } else {
      const res = await post(`/guzhe/shop/delete`, {
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
    const res = await post(`/guzhe/shop/status`, {
      status: checked ? 1 : 0,
      changeId: id,
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

  handleCancel = () => {
    this.setState({
      content: '',
      isShowNotice: 0,
      entryConfig: {},
      showModal: false,
    });
  };

  handleOk = async () => {
    const res = await post(`/guzhe/system/basic/config/merchant-notice/update`, {
      configData: Object.entries(this.state.entryConfig).map(([key, value]) => {
        if (key === 'show_merchant_notice') {
          value = this.state.isShowNotice;
        } else if (key === 'merchant_notice') {
          value = this.state.content;
        }
        return { key, value };
      }),
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

  getEntryGuidelines = async () => {
    const res = await post(`/guzhe/system/basic/config/merchant-notice`);
    if (res && res.code == 10000) {
      const data = res.data.reduce((pre, item) => {
        pre[item.key] = item.value;
        return pre;
      }, {})
      this.setState({
        entryConfig: data,
        content: data['merchant_notice'] || '',
        isShowNotice: +data['show_merchant_notice'] || 0,
        showModal: true,
      });
    } else {
      message.error(res?.msg);
    }
  }

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
        title: 'logo',
        dataIndex: 'coverImageUrl',
        render: (text, record) => {
          return (
            <>
              <img
                src={text}
                alt=""
                style={{ width: 103.5, height: 37.5, objectFit: 'contain' }}
              />
            </>
          );
        },
      },
      {
        fixed: 'left',
        title: '商家名称',
        dataIndex: 'name',
      },
      {
        title: '所属商超',
        width: 200,
        render: (res, record) =>
          record.circleNameList && (
            <Tooltip
              color="#fff"
              placement="top"
              title={record.circleNameList.map((ax) => ax).join('、')}
            >
              {record.circleNameList.map((ax) => ax).join('、')}
            </Tooltip>
          ),
      },
      {
        title: '行业类别',
        dataIndex: 'industryCategoryList',
        render: (res, record) => <div>{res?.map((i) => this.state.typeList.find((item) => item.id == i.industryCategoryId)?.name).join('、')}</div>,
      },
      {
        title: '排序',
        dataIndex: 'recommendOrder',
      },
      {
        title: '店长',
        dataIndex: 'userName',
      },
      {
        title: '联系电话',
        dataIndex: 'phone',
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
        title: '资质认证',
        dataIndex: 'qualificationCert',
        render: (res) => (
          <div
            style={{
              color:
                res == 0
                  ? '#189eff'
                  : res == 1
                    ? '#f59a23'
                    : res == 2
                      ? '#27b43e'
                      : '#da1c30',
            }}
          >
            {res == 0
              ? '未认证'
              : res == 1
                ? '待审核'
                : res == 2
                  ? '已通过'
                  : '已驳回'}
          </div>
        ),
      },
      {
        title: '费率',
        dataIndex: 'rate',
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
          <div
            style={{
              color: res == 0 ? '#27b43e' : res == 2 ? '#da1c30' : '#f59a23',
            }}
          >
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
                  history.push(`/shangjiaManagement/shangjiaInfomation?id=${record.id}&type=edit&shopName=${record.name}`);
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
                  <Form.Item label="商家名称" name="keyword">
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="所属商超" name="searchId">
                    <Select
                      showSearch
                      placeholder="请选择"
                      optionFilterProp="children"
                    >
                      {this.state.circleList.map((sa) => (
                        <Option value={sa.id}>{sa.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="行业类别" name="searchField5">
                    <Select showSearch placeholder="请选择">
                      {this.state.typeList.map((sa) => (
                        <Option value={sa.id}>{sa.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="启用状态" name="searchIntStatus">
                    <Select showSearch placeholder="请选择">
                      <Option value={1}>开启</Option>
                      <Option value={0}>关闭</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="资质认证" name="searchField3">
                    <Select showSearch placeholder="请选择">
                      <Option value={0}>未认证</Option>
                      <Option value={1}>待审核</Option>
                      <Option value={2}>已通过</Option>
                      <Option value={3}>已驳回</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={5}>
                  <Form.Item label="商家状态" name="searchField4">
                    <Select showSearch placeholder="请选择">
                      <Option value={0}>正常</Option>
                      <Option value={1}>禁用</Option>
                      <Option value={2}>已注销</Option>
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
                  商家信息管理
                </h1>
              </Col>
              <Col span={12} style={{ textAlign: 'right' }}>
                <Button
                  type="primary"
                  onClick={() => {
                    console.log(1);
                    history.push(`/shangjiaManagement/shangjiaInfomation?type=add`)
                  }}
                  style={{ marginRight: 15 }}
                >
                  +新增商家
                </Button>
                <Button
                  style={{ marginRight: 15 }}
                  variant="outlined"
                  color="blue"
                  onClick={this.getEntryGuidelines}
                >
                  入驻前须知
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
            title="入驻前须知"
            width="35%"
            visible={this.state.showModal}
            onOk={this.handleOk}
            onCancel={this.handleCancel}
            destroyOnClose
          >
            <Form
              labelCol={{ span: 6 }}
              wrapperCol={{ span: 18 }}
            >
              <Form.Item
                label={<div><span style={{ color: 'red' }}>*</span>是否展示入驻前须知</div>}
              >
                <Radio.Group value={this.state.isShowNotice} onChange={(e) => this.setState({ isShowNotice: e.target.value })}>
                  <Radio value={0}>否</Radio>
                  <Radio value={1}>是</Radio>
                </Radio.Group>
              </Form.Item>
              <Form.Item
                label={
                  <span>
                    {this.state.isShowNotice == 1 && <span style={{ color: 'red' }}>*</span>}
                    入驻前须知
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
        </PageContainer>
      </Spin>
    );
  }
}

export default Shangjia;
