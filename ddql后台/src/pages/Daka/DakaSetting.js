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
  Tabs,
  Upload,
  Alert,
  InputNumber,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { PageContainer } from '@ant-design/pro-layout';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { history, connect } from 'umi';
import { urlName } from '@/utils/utils';
import ImgCrop from 'antd-img-crop';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TabPane } = Tabs;

class DakaSetting extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  formRefsd = React.createRef();
  state = {
    activityKey: '1',
    spinning: false,
    xxx: true,
    roleData: [
      { roleId: 1, roleName: '哈哈' },
      { roleId: 2, roleName: '哈哈333' },
    ],
    thumbnailFileList: [],
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      url: `/ddql/checkInSettings/get`,
      method: 'POST',
      payload: {},
      myData: (res) => {
        if (res && res.code === 10000) {
          if (res.data) {
            this.setState({
              id: res.data.id,
            });
            if (this.state.activityKey == 1) {
              this.formRef.current.setFieldsValue({
                targetSteps: res.data.targetSteps,
                stepsGoldCoin: res.data.stepsGoldCoin,
                scanCodeGoldCoin: res.data.scanCodeGoldCoin,
              });
            }
            if (this.state.activityKey == 2) {
              this.formRefs.current.setFieldsValue({
                checkInInstruction: res.data.checkInInstruction,
              });
            }
            if (this.state.activityKey == 3) {
              const carouselFileList = [];
              const carouselImageUrls = res.data.withdrawalPictureList || [];
              carouselImageUrls.map((ress, index) => {
                carouselFileList.push({
                  uid: String(index + 1),
                  name: `image${index}.png`,
                  status: 'done',
                  url: ress,
                  response: { data: { url: ress } },
                });
              });
              this.setState({ thumbnailFileList: carouselFileList });
              this.formRefsd.current.setFieldsValue({
                withdrawalInstruction: res.data.withdrawalInstruction,
              });
            }
          }
        } else {
          message.error(res.msg);
        }
      },
    });
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

  handleUploadChange =
    (type) =>
    ({ file, fileList }) => {
      this.setState({ [type]: fileList }, () => {
        const { response = {} } = file;
        if (response.code == 10000) {
          const data = this.state[type];
          if (data[data.length - 1]) {
            data[data.length - 1].response.data.url =
              urlName + data[data.length - 1].response.data.url;
            this.setState({
              [type]: data,
            });
          }
        }
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
        url: `/api/admin/device/delete`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 200) {
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

  siteFrontReserve = (v, ids) => {
    console.log(ids);

    if (ids == undefined) {
      message.error('请选择需要操作的设备');
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          enable: v,
          id: ids.join(','),
        },
        url: `/api/admin/device/enable`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
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

  onFinish = (res) => {
    const params = {
      id: this.state.id,
    };
    if (this.state.activityKey == 1) {
      params.targetSteps = res.targetSteps;
      params.stepsGoldCoin = res.stepsGoldCoin;
      params.scanCodeGoldCoin = res.scanCodeGoldCoin;
    }
    if (this.state.activityKey == 2) {
      params.checkInInstruction = res.checkInInstruction;
    }
    if (this.state.activityKey == 3) {
      params.withdrawalInstruction = res.withdrawalInstruction;
      params.withdrawalPictureList = this.state.thumbnailFileList.map((cz) => cz.response.data.url);
    }
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: `/ddql/checkInSettings/update`,
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

  resets = () => {
    this.getData();
    this.setState({
      imageUrl: undefined,
    });
  };

  callback = (key) => {
    this.setState({
      activityKey: key,
    });
    this.getData();
  };

  beforeUpload = (file) => {
    const isJpgOrPng = file.name.indexOf('jfif') == -1;
    console.log(isJpgOrPng);
    if (!isJpgOrPng) {
      message.error('不符合类型文件');
    }

    return isJpgOrPng && true;
  };

  getBase64 = (img, callback) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result));
    reader.readAsDataURL(img);
  };

  handleChange = (info) => {
    if (info.file.status === 'uploading') {
      this.setState({ loading: true });
      return;
    }
    if (info.file.status === 'done') {
      console.log(info);
      this.setState({
        imageUrl: urlName + info.file.response.data.url,
        loading: false,
      });

      message.success({ content: '上传成功', duration: 0.7 });
    }
  };

  render() {
    const props = {
      grid: false,
      // width: 1100,
      // height: 312,
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 600,
    };

    // 图片格式校验（限制为jpg/jpeg/png）
    const beforeUpload = (file) => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        message.error('仅支持上传 JPG/PNG 格式的图片！');
        return false;
      }
      return true;
    };
    const {
      list = [],
      selectedRowKeys,
      NewRoles,
      edit,
      listss = [],
      imageUrl,
      loading,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
    };
    const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
    );

    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
      },
      {
        title: '图标',
        dataIndex: 'icon',
        render: (text, record) => {
          return (
            <>
              <img src={text} alt="" style={{ width: 100, height: 50, objectFit: 'cover' }} />
            </>
          );
        },
      },
      {
        title: '名称',
        dataIndex: 'name',
      },
      {
        title: '别名',
        dataIndex: 'alias',
      },
      {
        title: '说明',
        dataIndex: 'intro',
      },
      {
        title: '是否展示在预订模块',
        dataIndex: 'is_show',
        render: (text, record) => {
          return (
            <div>
              <span>
                {text == 1 ? <span className="luSe">是</span> : <span className="huangse">否</span>}
              </span>
            </div>
          );
        },
      },
      {
        title: '场馆数量',
        dataIndex: 'gym_total',
      },
      {
        title: '主场设置',
        dataIndex: 'master_gym',
        render: (text, record) => {
          return (
            <>
              {text ? (
                <a onClick={() => this.Venue(text.id, record.id)}>{text.name}</a>
              ) : (
                <a onClick={() => this.Venue(undefined, record.id)}>未设置</a>
              )}
            </>
          );
        },
      },
      {
        title: '创建时间',
        dataIndex: 'updated_at',
      },
      {
        title: '操作',
        render: (text, record) => {
          return (
            <div>
              <a onClick={() => this.showModal(false)}>编辑</a>

              {record.is_show == 1 ? (
                <a className="mL15" onClick={() => this.siteFrontReserve(0, [record.id])}>
                  关闭前台预订展示
                </a>
              ) : (
                <a className="mL15" onClick={() => this.siteFrontReserve(1, [record.id])}>
                  开启前台预订展示
                </a>
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
      <div className="zxcv">
        <div className="asd">
          <PageContainer
            header={{
              title: ``,
            }}
          >
            <Spin spinning={this.state.spinning}>
              <div style={{ backgroundColor: '#f0f2f5', marginBottom: 15 }}>
                <div style={{ backgroundColor: '#fff' }}>
                  <Tabs defaultActiveKey="1" onChange={this.callback} tabBarGutter={54}>
                    <TabPane tab="步数打卡设置" key="1">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          <div
                            style={{
                              backgroundColor: '#f0f2f5',
                              paddingBottom: 15,
                            }}
                          >
                            <div
                              style={{
                                padding: 24,
                                backgroundColor: '#fff',
                                minHeight: window.innerHeight - 280,
                              }}
                            >
                              <span style={{ fontSize: 18 }}>
                                <b>步数打卡设置</b>
                              </span>
                              <Form
                                layout="vertical"
                                ref={this.formRef}
                                onFinish={this.onFinish}
                                labelCol={{ span: 3 }}
                                wrapperCol={{ span: 10 }}
                                style={{ marginTop: 25, marginLeft: 40 }}
                              >
                                <Form.Item
                                  label={
                                    <span>
                                      <span style={{ color: 'red' }}>*</span>目标步数
                                    </span>
                                  }
                                >
                                  <Form.Item
                                    name="targetSteps"
                                    noStyle
                                    rules={[{ required: true, message: '请输入!' }]}
                                  >
                                    <InputNumber />
                                  </Form.Item>
                                  <div style={{ color: '#ccc' }}>达到目标步数小程序自动打卡</div>
                                </Form.Item>

                                <Form.Item
                                  label={
                                    <span>
                                      <span style={{ color: 'red' }}>*</span>步数打卡发放的金币数量
                                    </span>
                                  }
                                >
                                  <Form.Item
                                    name="stepsGoldCoin"
                                    noStyle
                                    rules={[{ required: true, message: '请输入!' }]}
                                  >
                                    <InputNumber />
                                  </Form.Item>
                                  <span style={{ color: '#333333', paddingLeft: 6 }}>个</span>
                                </Form.Item>
                                <Form.Item
                                  label={
                                    <span>
                                      <span style={{ color: 'red' }}>*</span>扫码打卡发放的金币数量
                                    </span>
                                  }
                                >
                                  <Form.Item
                                    name="scanCodeGoldCoin"
                                    noStyle
                                    rules={[{ required: true, message: '请输入!' }]}
                                  >
                                    <InputNumber />
                                  </Form.Item>
                                  <span style={{ color: '#333333', paddingLeft: 6 }}>个</span>
                                </Form.Item>
                                <Form.Item wrapperCol={{ span: 10 }}>
                                  <Button type="primary" htmlType="submit">
                                    保存
                                  </Button>
                                  <Button style={{ marginLeft: 15 }} onClick={this.resets}>
                                    重置
                                  </Button>
                                </Form.Item>
                              </Form>
                            </div>
                          </div>
                        </div>
                      </div>
                    </TabPane>
                    {/* <TabPane tab="打卡说明" key="2">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          <div
                            style={{
                              backgroundColor: '#f0f2f5',
                              paddingBottom: 15,
                            }}
                          >
                            <div
                              style={{
                                padding: 24,
                                backgroundColor: '#fff',
                                minHeight: window.innerHeight - 310,
                              }}
                            >
                              <span style={{ fontSize: 18 }}>
                                <b>打卡说明</b>
                              </span>
                              <Form
                                layout="vertical"
                                ref={this.formRefs}
                                onFinish={this.onFinish}
                                labelCol={{ span: 3 }}
                                wrapperCol={{ span: 10 }}
                                style={{ marginTop: 25, marginLeft: 40 }}
                              >
                                <Form.Item
                                  label={
                                    <span>
                                      <span style={{ color: 'red' }}>*</span>分享海报
                                    </span>
                                  }
                                >
                                  <Form.Item noStyle>
                                    <ImgCrop {...props} aspect={16/9}>
                                      <Upload
                                        action="/ddql/file/upload"
                                        listType="picture-card"
                                        fileList={this.state.thumbnailFileList}
                                        onChange={this.handleUploadChange('thumbnailFileList')}
                                        // onPreview={this.handlePreview}
                                        beforeUpload={beforeUpload}
                                        accept="image/jpeg,image/png"
                                        headers={{ token: localStorage.getItem('token') }}
                                      >
                                        {this.state.thumbnailFileList.length < 6 && uploadButton}
                                      </Upload>
                                    </ImgCrop>
                                  </Form.Item>
                                  <span style={{ color: '#ccc' }}>
                                    用户体现分享随机分配一张，建议尺寸1100*625px，最多上传6张，支持在线裁剪尺寸，图片格式支持jpg/jpeg/png
                                  </span>
                                </Form.Item>
                                <Form.Item
                                  label={
                                    <span>
                                      <span style={{ color: 'red' }}>*</span>打卡说明
                                    </span>
                                  }
                                >
                                  <Form.Item
                                    name="checkInInstruction"
                                    noStyle
                                    rules={[{ required: true, message: '请输入!' }]}
                                  >
                                    <Input.TextArea autoSize={{ minRows: 4 }} />
                                  </Form.Item>
                                </Form.Item>
                                <Form.Item wrapperCol={{ span: 10 }}>
                                  <Button type="primary" htmlType="submit">
                                    保存
                                  </Button>
                                  <Button style={{ marginLeft: 15 }} onClick={this.resets}>
                                    重置
                                  </Button>
                                </Form.Item>
                              </Form>
                            </div>
                          </div>
                        </div>
                      </div>
                    </TabPane> */}
                    <TabPane tab="提现说明" key="3">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          <div
                            style={{
                              backgroundColor: '#f0f2f5',
                              paddingBottom: 15,
                            }}
                          >
                            <div
                              style={{
                                padding: 24,
                                backgroundColor: '#fff',
                                minHeight: window.innerHeight - 280,
                              }}
                            >
                              <span style={{ fontSize: 18 }}>
                                <b>提现说明</b>
                              </span>
                              <Form
                                layout="vertical"
                                ref={this.formRefsd}
                                onFinish={this.onFinish}
                                labelCol={{ span: 3 }}
                                wrapperCol={{ span: 10 }}
                                style={{ marginTop: 25, marginLeft: 40 }}
                              >
                                <Form.Item
                                  label={
                                    <span>
                                      <span style={{ color: 'red' }}>*</span>分享海报
                                    </span>
                                  }
                                >
                                  <Form.Item noStyle>
                                    {/* <ImgCrop {...props} aspect={16 / 9}> */}
                                      <Upload
                                        action="/ddql/file/upload"
                                        listType="picture-card"
                                        fileList={this.state.thumbnailFileList}
                                        onChange={this.handleUploadChange('thumbnailFileList')}
                                        // onPreview={this.handlePreview}
                                        beforeUpload={beforeUpload}
                                        accept="image/jpeg,image/png"
                                        headers={{ token: localStorage.getItem('token') }}
                                      >
                                        {this.state.thumbnailFileList.length < 6 && uploadButton}
                                      </Upload>
                                    {/* </ImgCrop> */}
                                  </Form.Item>
                                  <span style={{ color: '#ccc' }}>
                                    用户体现分享随机分配一张，建议尺寸1100*625px，最多上传6张，支持在线裁剪尺寸，图片格式支持jpg/jpeg/png
                                  </span>
                                </Form.Item>
                                <Form.Item
                                  label={
                                    <span>
                                      <span style={{ color: 'red' }}>*</span>提现说明
                                    </span>
                                  }
                                >
                                  <Form.Item
                                    name="withdrawalInstruction"
                                    noStyle
                                    rules={[{ required: true, message: '请输入!' }]}
                                  >
                                    <Input.TextArea autoSize={{ minRows: 4 }} />
                                  </Form.Item>
                                </Form.Item>
                                <Form.Item wrapperCol={{ span: 10 }}>
                                  <Button type="primary" htmlType="submit">
                                    保存
                                  </Button>
                                  <Button style={{ marginLeft: 15 }} onClick={this.resets}>
                                    重置
                                  </Button>
                                </Form.Item>
                              </Form>
                            </div>
                          </div>
                        </div>
                      </div>
                    </TabPane>
                  </Tabs>
                </div>
              </div>
            </Spin>
          </PageContainer>
        </div>
      </div>
    );
  }
}

export default connect()(DakaSetting);
