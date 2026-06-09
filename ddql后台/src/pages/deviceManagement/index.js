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
  Switch,
  InputNumber,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import {
  LoadingOutlined,
  PlusOutlined,
  UploadOutlined,
  MinusCircleOutlined,
} from '@ant-design/icons';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { history, connect } from 'umi';
import Kaika from './components/Kaika';
import CKEditor from 'react-ckeditor-wrapper';
import { urlName } from '@/utils/utils';
import moment from 'moment';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const { TabPane } = Tabs;

class Member extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    xxx: true,
    rightsItems: [{ content: '' }],
    memberInfo: {},
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {},
      url: `/ddql/vipCard/select`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          if (res.data) {
            this.setState({
              memberInfo: res.data || {},
              imageUrl: res.data.image,
              sellType: res.data.sellType,
              content: res.data.benefitDetail,
            });
            this.formRef.current.setFieldsValue({
              name: res.data.name,
              shortName: res.data.shortName,
              status: res.data.status == 1 ? true : false,
              sellType: res.data.sellType,
              time:
                res.data.sellType == 1
                  ? [
                      moment(res.data.startSellTime, 'YYYY-MM-DD'),
                      moment(res.data.endSellTime, 'YYYY-MM-DD'),
                    ]
                  : undefined,
              benefitList: this.state.rightsItems.map((as) => as.content),
              price: res.data.price / 100,
            });
          }

          // this.formRef.current.setFieldsValue(object);
        } else {
          message.error(res.message);
        }
      },
    });
  };

  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };
  onSelectChange = (selectedRowKeys) => {
    //触发表单筛选
    console.log('selectedRowKeys changed: ', selectedRowKeys);
    this.setState({ selectedRowKeys });
  };
  resets = () => {
    this.getData();
    this.setState({
      addUrl: undefined,
      imageUrl: undefined,
    });
  };
  onFinish = (res) => {
    this.formRef.current.validateFields().then((res) => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          name: res.name,
          shortName: res.shortName,
          status: res.status ? 1 : 0,
          sellType: res.sellType,
          startSellTime:
            res.sellType == 1 ? moment(res.time[0]).format('YYYY-MM-DD 00:00:00') : undefined,
          endSellTime:
            res.sellType == 1 ? moment(res.time[1]).format('YYYY-MM-DD 23:59:59') : undefined,
          image: this.state.imageUrl,
          benefitList: this.state.rightsItems.map((as) => as.content),
          benefitDetail: this.state.content,
          price: res.price * 100,
        },
        url: `/ddql/vipCard/save`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            // this.getData();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    });
  };

  callback = (key) => {
    this.setState(
      {
        xxx: false,
      },
      () => {
        this.setState({
          xxx: true,
        });
      },
    );
  };
  handleTabChange = (activeKey) => {
    this.setState({ activeTab: activeKey });
    if (activeKey === '1') {
    } else if (activeKey === '2') {
    }
  };
  // 添加新的权益项
  addRightItem = () => {
    const newItem = {
      id: `item-${Date.now()}`,
      content: '',
    };
    this.setState({
      rightsItems: [...this.state.rightsItems, newItem],
    });
  };

  // 处理拖拽结束
  onDragEnd = (result) => {
    if (!result.destination) return;

    const items = Array.from(this.state.rightsItems);
    const [reorderedItem] = items.splice(result.source.index, 1);
    items.splice(result.destination.index, 0, reorderedItem);

    this.setState({
      rightsItems: items,
    });
  };

  // 更新权益项内容
  updateRightItem = (id, value) => {
    this.setState({
      rightsItems: this.state.rightsItems.map((item) =>
        item.id === id ? { ...item, content: value } : item,
      ),
    });
  };

  // 删除权益项
  removeRightItem = (id) => {
    this.setState({
      rightsItems: this.state.rightsItems.filter((item) => item.id !== id),
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
      const { response = {} } = info.file;
      if (response.code == 10000) {
        this.setState({
          imageUrl: urlName + info.file.response.data.url,
        });

        message.success({ content: '上传成功', duration: 0.7 });
      }
      // Get this url from response in real world.
      this.getBase64(info.file.originFileObj, (imageUrl) =>
        this.setState({
          // imageUrl,
          uditUrl: imageUrl,
          loading: false,
        }),
      );
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
    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',

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
          if (info.file.status === 'done') {
            console.log(info.file);
            const url = info.file.response.data.uri;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', url);

            ckeditor.instance.insertElement(ele);
            // }
          }
        },
      };
    };
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
              <div style={{ marginBottom: 15 }}>
                {this.state.activeTab == 2 ? (
                  <Row justify="end" style={{ width: '100%', position: 'absolute', top: '-30px' }}>
                    <Col style={{ textAlign: 'center' }} span={2}>
                      <span style={{ color: '#ccc' }}>会员总人数</span>
                      <h1 style={{ marginBottom: 0, marginTop: 2 }}>
                        <span style={{ fontSize: '14px' }}>0人</span>
                      </h1>
                      <div
                        style={{
                          borderRight: '1px solid #e9e9e9',
                          height: 35,
                          float: 'right',
                          position: 'absolute',
                          top: 12,
                          right: 0,
                        }}
                      />
                    </Col>
                    <Col style={{ textAlign: 'center' }} span={2}>
                      <span style={{ color: '#ccc' }}>今日开卡金额</span>
                      <h1 style={{ marginBottom: 0, marginTop: 2 }}>
                        <span style={{ fontSize: '14px' }}>0元</span>
                      </h1>
                      <div
                        style={{
                          borderRight: '1px solid #e9e9e9',
                          height: 35,
                          float: 'right',
                          position: 'absolute',
                          top: 12,
                          right: 0,
                        }}
                      />
                    </Col>
                    <Col style={{ textAlign: 'center' }} span={2}>
                      <span style={{ color: '#ccc' }}>累计开卡总额</span>
                      <h1 style={{ marginBottom: 0, marginTop: 2 }}>
                        <span style={{ fontSize: '14px' }}>0元</span>
                      </h1>
                      <div
                        style={{
                          borderRight: '1px solid #e9e9e9',
                          height: 35,
                          float: 'right',
                          position: 'absolute',
                          top: 12,
                          right: 0,
                        }}
                      />
                    </Col>
                    <Col style={{ textAlign: 'center' }} span={2}>
                      <span style={{ color: '#ccc' }}>今日消费总额</span>
                      <h1 style={{ marginBottom: 0, marginTop: 2 }}>
                        <span style={{ fontSize: '14px' }}>0元</span>
                      </h1>
                      <div
                        style={{
                          borderRight: '1px solid #e9e9e9',
                          height: 35,
                          float: 'right',
                          position: 'absolute',
                          top: 12,
                          right: 0,
                        }}
                      />
                    </Col>
                    <Col style={{ textAlign: 'center' }} span={2}>
                      <span style={{ color: '#ccc' }}>累计消费总额</span>
                      <h1 style={{ marginBottom: 0, marginTop: 2 }}>
                        <span style={{ fontSize: '14px' }}>0元</span>
                      </h1>
                      <div
                        style={{
                          borderRight: '1px solid #e9e9e9',
                          height: 35,
                          float: 'right',
                          position: 'absolute',
                          top: 12,
                          right: 0,
                        }}
                      />
                    </Col>
                  </Row>
                ) : (
                  <Row style={{ width: '100%', position: 'absolute', top: '-30px' }} justify="end">
                    <Col style={{ textAlign: 'center' }} span={2}>
                      <span style={{ color: '#ccc' }}>会员总人数</span>
                      <h1 style={{ marginBottom: 0, marginTop: 2 }}>
                        <span style={{ fontSize: '14px' }}>0人</span>
                      </h1>
                      <div
                        style={{
                          borderRight: '1px solid #e9e9e9',
                          height: 35,
                          float: 'right',
                          position: 'absolute',
                          top: 12,
                          right: 0,
                        }}
                      />
                    </Col>
                    <Col style={{ textAlign: 'center' }} span={2}>
                      <span style={{ color: '#ccc' }}>今日会员开卡数</span>
                      <h1 style={{ marginBottom: 0, marginTop: 2 }}>
                        <span style={{ fontSize: '14px' }}>0张</span>
                      </h1>
                      <div
                        style={{
                          borderRight: '1px solid #e9e9e9',
                          height: 35,
                          float: 'right',
                          position: 'absolute',
                          top: 12,
                          right: 0,
                        }}
                      />
                    </Col>

                    <Col style={{ textAlign: 'center' }} span={2}>
                      <span style={{ color: '#ccc' }}>今日会员消费总额</span>
                      <h1 style={{ marginBottom: 0, marginTop: 2 }}>
                        <span style={{ fontSize: '14px' }}>0元</span>
                      </h1>
                      <div
                        style={{
                          borderRight: '1px solid #e9e9e9',
                          height: 35,
                          float: 'right',
                          position: 'absolute',
                          top: 12,
                          right: 0,
                        }}
                      />
                    </Col>
                  </Row>
                )}
                <Tabs
                  defaultActiveKey="1"
                  activeKey={this.state.activeTab}
                  onChange={this.handleTabChange}
                >
                  <TabPane tab="会员卡信息" key="1">
                    <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                      <div style={{ backgroundColor: '#fff' }}>
                        <div style={{ padding: 24, backgroundColor: '#fff' }}>
                          <span style={{ fontSize: 18 }}>
                            <b>会员卡信息</b>
                          </span>
                          <Form
                            ref={this.formRef}
                            onFinish={this.onFinish}
                            labelCol={{ span: 3 }}
                            wrapperCol={{ span: 7 }}
                            style={{ marginTop: 25 }}
                          >
                            {/* 会员卡名称 */}
                            <Form.Item
                              label="会员卡名称"
                              name="name"
                              rules={[{ required: true, message: '请输入会员卡名称' }]}
                            >
                              <Input placeholder="请输入" />
                            </Form.Item>

                            {/* 简称 */}
                            <Form.Item
                              label="简称"
                              name="shortName"
                              rules={[{ required: true, message: '请输入简称' }]}
                            >
                              <Input placeholder="请输入" />
                            </Form.Item>
                            <div
                              style={{
                                color: '#ccc',
                                marginTop: -10,
                                marginBottom: 10,
                                paddingLeft: '12.6%',
                              }}
                            >
                              简称唯一，例如：VIP、SVIP
                            </div>

                            {/* 售价 */}

                            <Form.Item
                              label={
                                <div>
                                  <span style={{ color: 'red' }}>*</span>售价
                                </div>
                              }
                            >
                              <Form.Item
                                noStyle
                                name="price"
                                rules={[{ required: true, message: '请输入价格!' }]}
                              >
                                <InputNumber min={0} placeholder="请输入" />
                              </Form.Item>
                              <span style={{ paddingLeft: 4 }}>元</span>
                            </Form.Item>

                            {/* 上架状态 */}
                            <Form.Item
                              label="上架状态"
                              name="status"
                              valuePropName="checked"
                              initialValue={true}
                              rules={[{ required: true }]}
                            >
                              <Switch />
                            </Form.Item>

                            {/* 售卖时间 */}
                            <Form.Item
                              label={
                                <div>
                                  <span style={{ color: 'red' }}>*</span>售卖时间
                                </div>
                              }
                            >
                              <div style={{ display: 'flex', width: 500, alignItems: 'center' }}>
                                <Form.Item
                                  noStyle
                                  name="sellType"
                                  rules={[{ required: true, message: '请选择' }]}
                                >
                                  <Radio.Group
                                    style={{ flex: 1 }}
                                    onChange={(e) => this.setState({ sellType: e.target.value })}
                                  >
                                    <Radio value={0}>永久售卖</Radio>
                                    <Radio value={1}>限时售卖</Radio>
                                  </Radio.Group>
                                </Form.Item>
                                {this.state.sellType == 1 && (
                                  <Form.Item
                                    noStyle
                                    name="time"
                                    rules={[{ required: true }]}
                                    style={{ marginLeft: 10 }}
                                  >
                                    <RangePicker format="YYYY-MM-DD" />
                                  </Form.Item>
                                )}
                              </div>
                            </Form.Item>

                            {/* 卡面图片 */}
                            <Form.Item
                              label={
                                <div>
                                  <span style={{ color: 'red' }}>*</span>卡面图片
                                </div>
                              }
                              name="cardImage"
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
                              <div style={{ color: '#ccc', marginTop: 8 }}>建议尺寸 690*250px</div>
                            </Form.Item>
                            <Form.Item label="其他权益" name="otherRights">
                              <Alert
                                message="按住鼠标拖拽可调整展示顺序"
                                type="warning"
                                showIcon
                                style={{ marginBottom: 10, textAlign: 'left' }}
                              />
                              <DragDropContext onDragEnd={this.onDragEnd}>
                                <Droppable droppableId="rightsItems">
                                  {(provided, snapshot) => (
                                    <div {...provided.droppableProps} ref={provided.innerRef}>
                                      {this.state.rightsItems.map((item, index) => (
                                        <React.Fragment key={item.id}>
                                          {index > 0 && !snapshot.isDraggingOver && (
                                            <div
                                              style={{
                                                height: 1,
                                                backgroundColor: '#f0f0f0',
                                                margin: '4px 0',
                                              }}
                                            />
                                          )}
                                          <Draggable
                                            key={item.id}
                                            draggableId={item.id}
                                            index={index}
                                          >
                                            {(provided) => (
                                              <div
                                                ref={provided.innerRef}
                                                {...provided.draggableProps}
                                                style={{
                                                  marginBottom: 8,
                                                  display: 'flex',
                                                  alignItems: 'center',
                                                  width: 456,
                                                  ...provided.draggableProps.style,
                                                  borderradius: '4px',
                                                }}
                                              >
                                                <div
                                                  {...provided.dragHandleProps}
                                                  style={{
                                                    padding: '4px 12px',
                                                    backgroundColor: '#fff',
                                                    border: '1px solid #d9d9d9',
                                                    borderRadius: 4,
                                                    flex: 1,
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    height: 50,
                                                  }}
                                                >
                                                  <Input
                                                    value={item.content}
                                                    onChange={(e) =>
                                                      this.updateRightItem(item.id, e.target.value)
                                                    }
                                                    placeholder="请输入权益内容"
                                                    style={{
                                                      border: 'none',
                                                      boxShadow: 'none',
                                                      padding: '4px 0',
                                                      width: '65%',
                                                    }}
                                                    bordered={false}
                                                  />
                                                </div>
                                                <Button
                                                  type="text"
                                                  danger
                                                  onClick={() => this.removeRightItem(item.id)}
                                                  style={{
                                                    marginLeft: 8,
                                                    padding: '0 4px',
                                                    color: '#d9d9d9',
                                                  }}
                                                  icon={
                                                    <MinusCircleOutlined
                                                      style={{ color: '#d9d9d9' }}
                                                    />
                                                  }
                                                />
                                              </div>
                                            )}
                                          </Draggable>
                                        </React.Fragment>
                                      ))}
                                      {provided.placeholder}
                                    </div>
                                  )}
                                </Droppable>
                              </DragDropContext>
                              <Button
                                type="dashed"
                                block
                                icon={<PlusOutlined />}
                                onClick={this.addRightItem}
                                style={{ marginTop: 8 }}
                              >
                                添加权益
                              </Button>
                            </Form.Item>

                            {/* 权益详情 */}
                            <Form.Item
                              label={
                                <span>
                                  <span style={{ color: 'red' }}>*</span>权益详情
                                </span>
                              }
                              rules={[{ required: true, message: '请输入!' }]}
                            >
                              <div style={{ position: 'relative', marginTop: '-15px', width: 600 }}>
                                <Upload
                                  showUploadList={false}
                                  accept={'image/*'}
                                  // headers={{
                                  //   Authorization: getToken()
                                  //
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
                                    uploadUrl: '/home/media/upload',
                                    removeDialogTabs: 'image:advanced;link:advanced',
                                  }}
                                  onChange={this.updateContent}
                                />
                              </div>
                            </Form.Item>

                            {/* 操作按钮 */}
                            <Form.Item wrapperCol={{ offset: 3, span: 10 }}>
                              <Button type="primary" htmlType="submit">
                                确定
                              </Button>
                              <Button style={{ marginLeft: 15 }} onClick={this.resets}>
                                取消
                              </Button>
                            </Form.Item>
                          </Form>
                        </div>
                      </div>
                    </div>
                  </TabPane>
                  <TabPane tab="开卡记录" key="2">
                    <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                      <div>{this.state.xxx && <Kaika />}</div>
                    </div>
                  </TabPane>
                </Tabs>
              </div>
            </Spin>
          </PageContainer>
        </div>
      </div>
    );
  }
}

export default connect()(Member);
