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
import ArticleManagement from './components/ArticleManagement';
import { urlName } from '@/utils/utils';
import { values } from 'lodash';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TabPane } = Tabs;

class NoticeNotice extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    xxx: true,
    roleData: [
      { roleId: 1, roleName: '哈哈' },
      { roleId: 2, roleName: '哈哈333' },
    ],
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
        //获取系统设置
        this.props.dispatch({
          type: 'myModel/getSetData',
          url: `/ddql/system/basic/config`,
          method: 'POST',
          myData: (res) => {
            this.setState({
              spinning: false,
            });
            if (res && res.code === 10000) {
              const object = {};
              const listss = res.data
                .filter((item) => item.key != 'login_page_pic')
                .map((item) => {
                  object[item.key] = item.value;
                  return item;
                });
              this.setState({
                listss,
                imageUrl: object?.logo,
              });

              this.formRef.current.setFieldsValue(object);

              this.props.dispatch({
                type: 'global/saveConfig',
                payload: {
                  version: object?.version,
                  miitbeian: object?.miitbeian,
                  logo: object?.logo,
                  name: object?.name,
                },
              });
            } else {
              message.error(res.msg);
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
    const { listss, imageUrl } = this.state;

    const configData = listss.map((item) => {
      let data = res[item.key] || '';
      if (item.key == 'logo') data = imageUrl;
      return {
        ...item,
        value: data,
      };
    });
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        configData,
      },
      url: `/ddql/system/basic/config/update`,
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
    if (key == '1') {
      this.getData();
    }
    this.setState({
      xxx: key == '1' ? false : true,
    });
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
                  <Tabs defaultActiveKey="1" onChange={this.callback}>
                    <TabPane tab="基础信息" key="1">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          <div
                            style={{
                              backgroundColor: '#f0f2f5',
                              paddingBottom: 15,
                            }}
                          >
                            <div style={{ padding: 24, backgroundColor: '#fff' }}>
                              <span style={{ fontSize: 18 }}>
                                <b>关于我们</b>
                              </span>
                              <Form
                                ref={this.formRef}
                                onFinish={this.onFinish}
                                labelCol={{ span: 3 }}
                                wrapperCol={{ span: 10 }}
                                style={{ marginTop: 25 }}
                              >
                                <Form.Item
                                  label={
                                    <span>
                                      <span style={{ color: 'red' }}>*</span>系统logo
                                    </span>
                                  }
                                >
                                  <Form.Item
                                    name="logo"
                                    noStyle
                                    rules={[{ required: true, message: '请输入!' }]}
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
                                          style={{
                                            width: '100%',
                                            height: '100%',
                                            objectFit: 'cover',
                                          }}
                                        />
                                      ) : (
                                        uploadButton
                                      )}
                                    </Upload>
                                  </Form.Item>
                                  <div style={{ color: '#ccc' }}>建议尺寸 476*75px</div>
                                </Form.Item>

                                <Form.Item label="系统名称" name="name">
                                  <Input placeholder="请输入" />
                                </Form.Item>
                                <Form.Item label="单位名称" name="org_name">
                                  <Input placeholder="请输入" />
                                </Form.Item>
                                <Form.Item label="联系地址" name="address">
                                  <Input placeholder="请输入" />
                                </Form.Item>
                                <Form.Item label="联系电话" name="phone">
                                  <Input placeholder="请输入" />
                                </Form.Item>
                                <Form.Item label="邮箱" name="email">
                                  <Input placeholder="请输入" />
                                </Form.Item>
                                <Form.Item label="版权信息" name="version">
                                  <Input placeholder="请输入" />
                                </Form.Item>
                                <Form.Item label="备案信息" name="miitbeian">
                                  <Input placeholder="请输入" />
                                </Form.Item>

                                <Form.Item wrapperCol={{ offset: 3, span: 10 }}>
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
                    <TabPane tab="文章管理" key="2">
                      <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                        <div style={{ backgroundColor: '#fff' }}>
                          {this.state.xxx && <ArticleManagement />}
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
