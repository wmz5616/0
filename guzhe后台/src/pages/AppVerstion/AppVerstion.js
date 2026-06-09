import { getToken } from '@/utils/authority';
import { post } from '@/utils/request';
import { urlName } from '@/utils/utils';
import { UploadOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import {
  Button,
  Col,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Radio,
  Row,
  Spin,
  Table,
  Upload,
} from 'antd';
import React from 'react';
const { TextArea } = Input;
//角色管理
class AppVerstion extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    status: '',
    modalVisible: false,
    carouselFileList: [],
    logVisible: false,
    logList: [],
    logPageNum: 1,
    logTotal: 0,
    logPageSize: 10,
    total: 0,
    circleList: [],
  };

  componentDidMount() {
    this.getData();
    this.getAdminList()
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
        //角色列表
        const res = await post(`/guzhe/appVersion/select`, {
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

  showLog = (record) => {
    this.setState({
      logVisible: true,
      record,
      logId: record.id,
    }, () => {
      this.getLogList();
    });
  };

  getLogList = async () => {
    const res = await post(`/guzhe/appVersion/log/select`, {
      // searchId: this.state.logId,
      searchStrField1: this.state.record.serialNumber,
      pageNum: this.state.logPageNum,
      pageSize: this.state.logPageSize,
    });
    if (res && res.code === 10000) {
      this.setState({
        logList: res.data.list,
        logTotal: res.data.total,

      });
    } else {
      message.error(res?.msg);
    }
  }

  closeLog = () => {
    this.setState({
      logVisible: false,
    });
  };

  // 删除函数
  deletes = async (searchId) => {
    const res = await post(`/guzhe/appVersion/delete`, {
      searchId,
    });
    if (res && res.code === 10000) {
      message.success(res.msg);
      this.getData();
    } else {
      message.error(res?.msg);
    }
  };

  handleOk = () => {
    this.formRef.current.validateFields().then(async (values) => {
      const params = {
        remark: values.remark,
        serialNumber: values.serialNumber,
        isPublish: values.isPublish,
        release: values.release,
        fileUrl: this.state.carouselFileList.map(
          (re) => re.response.data.url,
        )[0],
      };
      if (!this.state.isAdd) {
        params.id = this.state.id;
      }
      const res = await post(
        this.state.isAdd ? `/guzhe/appVersion/add` : `/guzhe/appVersion/update`,
        { ...params },
      );
      if (res && res.code === 10000) {
        message.success(res.msg);
        this.getData();
        this.setState({
          modalVisible: false,
        });
      } else {
        message.error(res?.msg);
      }
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
          if (response.code === 10000) {
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


  font = async (searchId) => {
    const res = await post(`/guzhe/appVersion/publish`, {
      searchId,
    });
    if (res && res.code === 10000) {
      message.success(res.msg);
      this.getData();
    }
    else {
      message.error(res?.msg);
    }
  };

  render() {
    const { list = [], selectedRowKeys, pageNum, total } = this.state;
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
        title: '发布日志',
        dataIndex: 'publishLog',
        render: (res, record) => (
          <div className="clickFont" onClick={() => this.showLog(record)}>
            查看
          </div>
        ),
      },
      // {
      //   title: '是否发布',
      //   dataIndex: 'isPublish',
      //   render: (text, record) => {
      //     return (
      //       <Switch
      //         unCheckedChildren="关"
      //         checkedChildren="开"
      //         checked={text === 1 ? false : true}
      //         onChange={async (value) => {
      //           const params = {
      //             id: record.id,
      //             remark: record.remark,
      //             serialNumber: record.serialNumber,
      //             isPublish: value ? 0 : 1,
      //             release: record.release,
      //             fileUrl: record.fileUrl,
      //           };
      //           const res = await post(`/guzhe/appVersion/update`, {
      //             ...params,
      //           });
      //           if (res && res.code === 10000) {
      //             message.success(res.msg);
      //             this.getData();
      //           } else {
      //             message.error(res?.msg);
      //           }
      //         }}
      //       />
      //     );
      //   },
      // },
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
        render: (text, record) => {
          return (
            <div style={{ display: 'flex' }}>
              <div style={{ display: 'flex', gap: 7 }}>
                {/* <span className="clickFont" onClick={() => this.font(record.id)}>发布</span> */}
                <span
                  className="clickFont"
                  onClick={() =>
                    this.setState(
                      {
                        modalVisible: true,
                        isAdd: false,
                        id: record.id,
                        info: record,
                      },
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
                                status: 'done',
                                name: record.fileUrl,
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
              </div>

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
    const columns1 = [
      {
        title: '序号',
        render: (text, record, index) =>
          (this.state.logPageNum - 1) * this.state.logPageSize + index + 1,
      },
      {
        title: '设备编号',
        dataIndex: 'serialNumber',
      },
      {
        title: '所属商超',
        dataIndex: 'supId',
        render: (res) => <div>{this.state.circleList.find((item) => item.id == res)?.name || ''}</div>,
      },
      {
        title: '下发状态',
        dataIndex: 'status',
        render: (text) => (
          <div style={{ color: text === 1 ? '#5cd668' : '#e65a6b' }}>
            {text === 1 ? '成功' : '失败'}
          </div>
        ),
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
      },
    ];

    const props = {
      maxCount: 1,
      name: 'file',
      action: '/guzhe/file/upload',
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
              scroll={{ x: 'max-content' }}
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
                      <span style={{ color: 'red' }}>*</span>版本编号
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
                <Form.Item
                  label="上传附件"
                  name="url"
                  rules={[{ required: true }]}
                >
                  <Upload
                    {...props}
                    fileList={this.state.carouselFileList}
                    onChange={this.handleUploadChange('carouselFileList')}
                    beforeUpload={(e) => {
                      console.log(e.type);
                      let type =
                        e.type === 'application/vnd.android.package-archive';
                      if (e.type != 'application/vnd.android.package-archive') {
                        message.info('上传文件类型不支持！');
                      }
                      return type;
                    }}
                  >
                    <Button icon={<UploadOutlined />}>上传文件</Button>
                  </Upload>
                </Form.Item>
                <Form.Item
                  label="是否发布"
                  name="isPublish"
                  rules={[{ required: true }]}
                >
                  <Radio.Group>
                    <Radio value={0}>是</Radio>
                    <Radio value={1}>否</Radio>
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
            <Modal
              title="查看发布日志"
              open={this.state.logVisible}
              onOk={this.closeLog}
              destroyOnHidden
              onCancel={this.closeLog}
              width='45%'
            >
              <Row gutter={16}>
                <Col span={8}>版本编号：{this.state.record?.serialNumber}</Col>
                <Col span={8}>release：{this.state.record?.release}</Col>
              </Row>
              <Table
                dataSource={this.state.logList}
                columns={columns1}
                pagination={{
                  showSizeChanger: false,
                  onChange: (page) => {
                    this.setState({
                      logPageNum: page,
                    });
                  },
                  pageSize: this.state.logPageSize,
                  total: this.state.logTotal,
                  current: this.state.logPageNum,
                }}
              />
            </Modal>
          </div>
        </Spin>
      </PageContainer>
    );
  }
}

export default AppVerstion;
