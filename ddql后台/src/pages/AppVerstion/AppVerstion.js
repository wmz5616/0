import React from 'react';
import { UserOutlined, LockOutlined, UploadOutlined } from '@ant-design/icons';
import DocumentTitle from 'react-document-title';
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
  Switch,
  Radio,
  Upload,
  Modal,
  InputNumber,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect } from 'umi';
import { getToken } from '@/utils/authority';
import { urlName } from '@/utils/utils';
const { TextArea } = Input;
// import { setToken } from '@/utils/authority';
const { Option } = Select;
//角色管理
class AppVerstion extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    status: '',
    modalVisible: false,
    carouselFileList: [],
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
        //角色列表
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            page: this.state.pageNum,
          },
          url: `/ddql/appVersion/select`,
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

  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    console.log('selectedRowKeys changed: ', selectedRowKeys);
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

  showModal = (add, edit) => {
    this.setState({
      isAdd: true,
      modalVisible: true,
      carouselFileList: [],
    });
  };

  // 删除函数
  //删除
  deletes = (e) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        id: e,
      },
      url: `/ddql/appVersion/delete`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  siteFrontReserve = (v, ids) => {
    console.log(ids);

    if (ids == undefined) {
      message.error('请选择需要操作的角色');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          enable: v,
          id: ids.join(','),
        },
        url: `/api/admin/role/enable`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
            this.setState({
              selectedRowKeys: [],
            });
            message.success(res.message);
            this.getData();
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
  };

  onFinish = (v) => {
    console.log(v);
    this.setState(
      {
        name: v.name,
        status: v.status,
        pageNum: 1,
      },
      () => {
        this.getData();
      },
    );
  };

  handleOk = () => {
    this.formRef.current.validateFields().then((values) => {
      const params = {
        remark: values.remark,
        serialNumber: values.serialNumber,
        isPublish: values.isPublish,
        release: values.release,
        fileUrl: this.state.carouselFileList.map((re) => re.response.data.url)[0],
      };
      if (!this.state.isAdd) {
        params.id = this.state.id;
      }
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        url: this.state.isAdd ? `/ddql/appVersion/add` : `/ddql/appVersion/update`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.getData();
            this.setState({
              modalVisible: false,
            });
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    });
  };

  handleUploadChange =
    (type) =>
    ({ file, fileList }) => {
      if (fileList[0].type != 'application/vnd.android.package-archive') {
        return;
      }
      this.setState({ [type]: fileList }, () => {
        const { response = {} } = file;
        if (response.code == 10000) {
          const data = this.state[type];
          if (data[data.length - 1]) {
            console.log(data[data.length - 1]);
            data[data.length - 1].response.data.url =
              urlName + data[data.length - 1].response.data.url;
            this.setState({
              [type]: data,
            });
          } else {
            this.setState({
              [type]: [],
            });
          }
        } else {
          if (response.msg) {
            message.info(response.msg);
          }
        }
      });
    };

  resets = () => {
    this.formRef.current.resetFields();
    this.setState(
      {
        name: undefined,
        status: '',
      },
      () => {
        this.getData();
      },
    );
  };

  render() {
    const { list = [], selectedRowKeys, NewRoles, pageNum, total } = this.state;
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
        title: '版本编号',
        dataIndex: 'serialNumber',
      },
      {
        title: 'release',
        dataIndex: 'release',
      },
      {
        title: '文件',
        dataIndex: 'fileUrl',
        render: (res) => (
          <div className="clickFont" onClick={() => window.open(res)}>
            点击下载
          </div>
        ),
      },
      {
        title: '是否发布',
        dataIndex: 'isPublish',
        render: (text, record) => {
          return (
            <Switch
              unCheckedChildren="关"
              checkedChildren="开"
              checked={text == 1 ? false : true}
              onChange={(value) => {
                const params = {
                  id: record.id,
                  remark: record.remark,
                  serialNumber: record.serialNumber,
                  isPublish: value ? 0 : 1,
                  release: record.release,
                  fileUrl: record.fileUrl,
                };
                this.props
                  .dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      ...params,
                    },
                    method: 'POST',
                    url: `/ddql/appVersion/update`,
                  })
                  .then((res) => {
                    if (res && res.code === 10000) {
                      message.success(res.msg);
                      this.getData();
                    } else {
                      message.error(res.msg);
                    }
                  });
              }}
            />
          );
        },
      },
      {
        title: '备注',
        dataIndex: 'remark',
        width: 380,
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
                onClick={() =>
                  this.setState(
                    { modalVisible: true, isAdd: false, id: record.id, info: record },
                    () => {
                      setTimeout(() => {
                        this.formRef.current.setFieldsValue({
                          remark: record.remark,
                          serialNumber: record.serialNumber,
                          isPublish: record.isPublish,
                          release: record.release,
                          url: true,
                        });
                        this.setState({
                          carouselFileList: [
                            {
                              uid: '1',
                              name: record.fileUrl,
                              status: 'done',
                              url: record.fileUrl,
                              response: { data: { url: record.fileUrl } },
                            },
                          ],
                        });
                      }, 200);
                    },
                  )
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
    const props = {
      maxCount: 1,
      name: 'file',
      action: '/ddql/file/upload',
      // multiple: true,
      headers: {
        token: getToken(),
      },
    };
    return (
      <PageContainer
        header={{
          title: ``,
        }}
      >
        <Spin spinning={this.state.spinning}>
          <div style={{ backgroundColor: '#fff', padding: 20 }}>
            <Row style={{ alignItems: 'center' }}>
              <Col span={6}>
                <h2 style={{ margin: 0 }}>APP版本管理</h2>
              </Col>
              <Col span={18}>
                <div style={{ textAlign: 'right' }}>
                  {/* <Popconfirm
                      placement="bottom"
                      title={
                        <>
                          <div>删除所有的入库异常记录</div>
                          <div>
                            <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                            <span style={{ color: '#ccc' }}>确定删除吗？</span>
                          </div>
                        </>
                      }
                      onConfirm={() => this.deletes()}
                      // onCancel={cancel}
                      okText="是"
                      cancelText="否"
                    >
                      <Button type="primary" style={{ marginRight: 15 }}>
                        清空异常记录
                      </Button>
                    </Popconfirm> */}
                  <Button type="primary" onClick={() => this.showModal(true)}>
                    新增
                  </Button>
                </div>
              </Col>
            </Row>

            <Table
              style={{ marginTop: 25 }}
              loading={this.state.loading}
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
              // scroll={{ x: '100%' }}
            />
            <Modal
              title={this.state.isAdd ? '新增版本' : '编辑版本'}
              open={this.state.modalVisible}
              onOk={this.handleOk}
              destroyOnHidden
              onCancel={() => {
                this.setState({
                  modalVisible: false,
                });
              }}
            >
              <Form
                ref={this.formRef}
                style={{ marginTop: 30 }}
                labelCol={{ span: 5 }}
                wrapperCol={{ span: 18 }}
              >
                <Form.Item
                  label={
                    <span>
                      <span style={{ color: 'red' }}>*</span>版本号
                    </span>
                  }
                >
                  <Form.Item
                    name="serialNumber"
                    noStyle
                    rules={[{ required: true, message: '请输入' }]}
                  >
                    <Input placeholder="请输入" />
                  </Form.Item>
                </Form.Item>
                <Form.Item label="上传附件" name="url" rules={[{ required: true }]}>
                  <Upload
                    {...props}
                    fileList={this.state.carouselFileList}
                    onChange={this.handleUploadChange('carouselFileList')}
                    beforeUpload={(e) => {
                      console.log(e.type);
                      let type = e.type == 'application/vnd.android.package-archive';
                      if (e.type != 'application/vnd.android.package-archive') {
                        message.info('上传文件类型不支持！');
                      }
                      return type;
                    }}
                  >
                    <Button icon={<UploadOutlined />}>上传文件</Button>
                  </Upload>
                </Form.Item>
                <Form.Item label="是否发布" name="isPublish" rules={[{ required: true }]}>
                  <Radio.Group>
                    <Radio value={1}>未发布</Radio>
                    <Radio value={0}>已发布</Radio>
                  </Radio.Group>
                </Form.Item>
                <Form.Item label="release" name="release">
                  <InputNumber placeholder="请输入" />
                </Form.Item>
                <Form.Item label="备注" name="remark">
                  <TextArea rows={4} placeholder="请输入" />
                </Form.Item>
              </Form>
            </Modal>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default connect()(AppVerstion);
