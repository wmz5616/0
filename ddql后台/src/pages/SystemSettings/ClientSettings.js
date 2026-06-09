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
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { PageContainer } from '@ant-design/pro-layout';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { history, connect } from 'umi';
import RotationMap from './components/RotationMap'; //轮播图
import ShortcutOperation from './components/ShortcutOperation'; //轮播图
import { urlName } from '@/utils/utils';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TabPane } = Tabs;

class NoticeNotice extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    xxx: true,
  };

  callback = (res) => {
    console.log(res);
    if (res == '1') {
      this.setState({
        xxx: true,
      });
    } else if (res == '2') {
      this.setState({
        xxx: false,
      });
    } else {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {},
        url: `/ddql/system/basic/config`,
        method: 'GET',
        myData: (res) => {
          if (res && res.code === 10000) {
            if (res.data.length != 0) {
              this.setState({
                imageUrl:
                  res.data.filter((x) => x.key == 'login_page_pic').length != 0
                    ? res.data.filter((x) => x.key == 'login_page_pic')[0].value
                    : undefined,
                addUrl:
                  res.data.filter((x) => x.key == 'login_page_pic').length != 0
                    ? res.data.filter((x) => x.key == 'login_page_pic')[0].value
                    : undefined,
              });
              this.formRef.current.setFieldsValue({
                login_banner:
                  res.data.filter((x) => x.key == 'login_page_pic').length != 0
                    ? res.data.filter((x) => x.key == 'login_page_pic')[0].value
                    : undefined,
              });
            }
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    }
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
      const { response = {} } = info.file;
      if (response.code == 10000) {
        this.setState({
          addUrl: urlName + info.file.response.data.url,
        });
      }

      message.success({ content: '上传成功', duration: 0.7 });
      // Get this url from response in real world.
      this.getBase64(info.file.originFileObj, (imageUrl) =>
        this.setState({
          imageUrl,
          uditUrl: imageUrl,
          loading: false,
        }),
      );
    }
  };

  onFinish = (v) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        configData: [
          {
            type: 1,
            remark: '登录页面配图',
            value: this.state.addUrl,
            key: 'login_page_pic',
          },
        ],
      },
      url: `/ddql/system/basic/config/update`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  resets = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        group: 'login',
      },
      url: `/api/admin/system/config`,
      method: 'GET',
      myData: (res) => {
        if (res && res.code === 200) {
          this.setState({
            imageUrl: res.data && res.data[0].value,
            addUrl: res.data && res.data[0].value,
          });
          this.formRef.current.setFieldsValue({
            login_banner: res.data && res.data[0].value,
          });
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  render() {
    const { imageUrl, loading } = this.state;
    const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
    );
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
                  <Tabs defaultActiveKey="1" onChange={this.callback}>
                    <TabPane tab="首页轮播图" key="1">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          {this.state.xxx && <RotationMap />}
                        </div>
                      </div>
                    </TabPane>
                    {/* <TabPane tab="首页快捷入口" key="2">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          {!this.state.xxx && <ShortcutOperation />}
                        </div>
                      </div>
                    </TabPane> */}
                    <TabPane tab="登录页面配图" key="3">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff', padding: 24 }}>
                          <h1 style={{ fontWeight: '600', fontSize: '18px' }}>登录页面配图</h1>
                          <Form
                            ref={this.formRef}
                            name="login_banner"
                            labelCol={{ span: 2 }}
                            wrapperCol={{ span: 8 }}
                            onFinish={this.onFinish}
                            autoComplete="off"
                          >
                            <Form.Item
                              label={
                                <span>
                                  <span style={{ color: 'red' }}>*</span>焦点图
                                </span>
                              }
                            >
                              <Form.Item
                                name="icon"
                                noStyle
                                rules={[{ required: true, message: '请输入' }]}
                              >
                                <Upload
                                  name="file"
                                  listType="picture-card"
                                  className="avatar-uploader"
                                  showUploadList={false}
                                  action="/ddql/file/upload"
                                  headers={{ token: localStorage.getItem('token') }}
                                  beforeUpload={this.beforeUpload}
                                  onChange={this.handleChange}
                                >
                                  {imageUrl ? (
                                    <img
                                      src={imageUrl}
                                      alt="avatar"
                                      style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                    />
                                  ) : (
                                    uploadButton
                                  )}
                                </Upload>
                              </Form.Item>
                              <span style={{ color: '#ccc' }}>建议尺寸690*306px</span>
                            </Form.Item>

                            <Form.Item wrapperCol={{ offset: 2, span: 8 }}>
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

export default connect()(NoticeNotice);
