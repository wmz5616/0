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
  Upload,
  InputNumber,
  Switch,
  DatePicker,
  Tabs,
} from 'antd';
import { history, connect, Link } from 'umi';
const { RangePicker } = DatePicker;
import CKEditor from 'react-ckeditor-wrapper';
const { TabPane } = Tabs;
import RechargeActivity from './RechargeActivity';

// 应用类型
const { Option } = Select;
const { Search } = Input;
const { TextArea } = Input;

const layout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

class DataConnection extends React.Component {
  formRef = React.createRef();

  state = {
    NewType: false,
    RecommendedSettings: false,
    confirmLoading: false,
    spinning: false,
    loading: false,
    imageUrl: '',
    info: {},
    aa: true,
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
        //详情
        const { dispatch, id } = this.props;
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchId: 1,
          },
          url: `/ddql/recharge/config/getInfo`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              this.setState({
                content: res.data.description,
                text1: res.data.status,
                xxx: res.data.enableCustomAmount,
              });
              this.formRef.current.setFieldsValue({
                enableRecharge: res.data.enableRecharge,
                auto_status: res.data.enableCustomAmount,
                min_amount: res.data.enableCustomAmount ? res.data.minAmount : undefined,
                criticalAmount: res.data.criticalAmount,
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

  updateContent = (value, index) => {
    this.setState({
      content: value,
    });
  };

  onFinish = (value) => {
    console.log(value);
    const { dispatch, id } = this.props;
    dispatch({
      type: 'myModel/getSetData',
      payload: {
        enableRecharge: value.enableRecharge?1:0,
        enableCustomAmount: value.auto_status ? 1 : 0,
        minAmount: value.min_amount,
        criticalAmount: value.criticalAmount,
        description: this.state.content,
        id: 1,
      },
      url: `/ddql/recharge/config/update`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
          message.success(res.msg);
        } else {
          message.error(res.msg);
        } 
      },
    });
  };

  onChangexx = (checked) => {
    console.log(`switch to ${checked}`);
    this.setState({
      xxx: checked,
    });
  };

  onChange = () => {
    this.setState(
      {
        aa: false,
      },
      () => {
        this.setState({
          aa: true,
        });
      },
    );
  };

  render() {
    const { gymTypelist = [], info = {}, xxx } = this.state;
    const { id } = this.props;
    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/api/common/upload',

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
    return (
      <div className="pp">
        <div style={{ paddingTop: 20 }}>
          <Tabs tabPosition={'left'} onChange={this.onChange}>
            <TabPane tab="基本设置" key="1">
              <h1 style={{ fontWeight: '600', fontSize: '18px', marginBottom: 20 }}>基本设置</h1>
              <Form
                onFinish={this.onFinish}
                layout="vertical"
                style={{ marginLeft: 20 }}
                ref={this.formRef}
              >
                <Form.Item
                  label="充值功能"
                  name="enableRecharge"
                  rules={[{ required: true, message: '请选择' }]}
                  valuePropName="checked"
                  initialValue={false}
                >
                  <Switch checkedChildren="开启" unCheckedChildren="关闭" />
                </Form.Item>

                <Form.Item
                  label={
                    <span>
                      自定义充值金额
                      <span style={{ color: '#ccc' }}>
                        &nbsp;&nbsp;如开启本功能，则需设置最低充值金额
                      </span>
                    </span>
                  }
                  name="auto_status"
                  rules={[{ required: true, message: '请选择' }]}
                  valuePropName="checked"
                  initialValue={false}
                >
                  <Switch
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                    onChange={this.onChangexx}
                  />
                </Form.Item>

                {xxx ? (
                  <Form.Item
                    label={
                      <span>
                        <span style={{ color: 'red' }}>*</span>最低充值金额
                      </span>
                    }
                  >
                    <Form.Item
                      name="min_amount"
                      noStyle
                      rules={[{ required: false, message: '请输入!' }]}
                    >
                      <InputNumber min={0} />
                    </Form.Item>
                    <span style={{ marginLeft: 10 }}>元</span>
                  </Form.Item>
                ) : (
                  ''
                )}

                <Form.Item>
                  <span>每当余额变更且余额低于</span>
                  <Form.Item
                    label={
                      <span>
                        充值活动指定使用范围
                        <span style={{ color: '#ccc' }}>
                          &nbsp;&nbsp;如开启本功能，则可在充值活动中设置充值活动所允许的使用场馆
                        </span>
                      </span>
                    }
                    name="criticalAmount"
                    noStyle
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <InputNumber min={0} />
                  </Form.Item>
                  <span style={{ paddingLeft: 10, paddingRight: 10 }}>元</span>
                  <span>给团体创建者和管理员发送消息提醒</span>
                </Form.Item>

                <Form.Item
                  label={
                    <span>
                      <span style={{ color: 'red' }}>*</span>充值说明
                    </span>
                  }
                  rules={[{ required: true, message: '请输入!' }]}
                >
                  <div style={{ position: 'relative', marginTop: '-15px', width: 900 }}>
                    <Upload
                      showUploadList={false}
                      accept={'image/*'}
                      // headers={{
                      //   Authorization: getToken()
                      //
                      {...uploadProps(1)}
                    >
                      <div className="zxccx" />
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
                            items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-'],
                          },
                          {
                            name: 'basicstyles',
                            items: ['Bold', 'Italic', 'Underline', '-', 'CopyFormatting'],
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
                        height: 350,
                        uploadUrl: '/home/media/upload',
                        removeDialogTabs: 'image:advanced;link:advanced',
                      }}
                      onChange={this.updateContent}
                    />
                  </div>
                </Form.Item>

                <Form.Item>
                  <Button type="primary" htmlType="submit">
                    保存
                  </Button>
                </Form.Item>
              </Form>
            </TabPane>

            <TabPane tab="充值活动" key="2">
              {this.state.aa && <RechargeActivity />}
            </TabPane>
          </Tabs>
        </div>
      </div>
    );
  }
}

// 7
// 绑定到本页面，此处就可以拿到请求的值，
// allModels所有models集合，mapping是具体的model的命名空间，mappingData命名空间里面你想取的值
export default connect((allModels) => ({}))(DataConnection);
