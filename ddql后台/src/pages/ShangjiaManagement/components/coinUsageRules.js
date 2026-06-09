import React from 'react';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Select,
  DatePicker,
  Upload,
  Modal,
  Radio,
  Switch,
  TimePicker,
  Alert,
  InputNumber,
  Table,
} from 'antd';
import { history, connect, Link } from 'umi';
import CKEditor from 'react-ckeditor-wrapper';
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
const { TextArea } = Input;
// import { setToken } from '@/utils/authority';

const { Option } = Select;
const { RangePicker } = DatePicker;

class CoinUsageRules extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    remark: false,
    ruleId: undefined,
  };

  componentDidMount() {
    const { type, id, disabled = false } = this.props;
    console.log(1111)
    this.setState(
      {
        type,
        id,
        disabled,
      },
      () => {
        type != 'add' && this.getData();
      },
    );
  }

  updateContent = (value, index) => {
    this.setState({
      remark: value,
    });
  };

  getData = () => {
    this.setState({
      spinning: true,
    });
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: this.state.id,
      },
      url: `/ddql/business/shop/coin/get`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
          const data = res.data;
          this.formRef.current.setFieldsValue({
            beginAmount: data.beginAmount,
            deduct: data.deduct,
            maxDeduct: data.maxDeduct,
            threshold: data.threshold,
          });
          this.setState({
            remark: data.remark,
            ruleId: data.id,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  onFinish = () => {
    this.formRef.current.validateFields().then((values) => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          beginAmount: values.beginAmount,
          deduct: values.deduct,
          maxDeduct: values.maxDeduct,
          threshold: values.threshold,
          shopId: this.state.id,
          remark: this.state.remark,
          id: this.state.ruleId || undefined,
        },
        url: this.state.ruleId ? '/ddql/business/shop/coin/update' : '/ddql/business/shop/coin/add',
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.getData()
          } else {
            message.error(res.msg);
          }
        },
      });
    });
  };

  render() {
    const { type, disabled } = this.state;
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

    return (
      <Spin spinning={this.state.spinning}>
        <Row align="middle" justify="space-between">
          <Col span={4}>
            <h2>用币规则</h2>
          </Col>
          <Col span={2}>
            <Button onClick={() => history.goBack()}>返回</Button>
          </Col>
        </Row>
        <Form
          ref={this.formRef}
          //   labelCol={{ span: 2 }}
          //   wrapperCol={{ span: 18 }}
          disabled={disabled}
          onFinish={this.onFinish}
          autoComplete="off"
        >
          <Row gutter={16}>
            <Col span={18}>
              <Form.Item
                label="起始金额（元）"
                name="beginAmount"
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber min={0} placeholder='请输入'/>
              </Form.Item>
            </Col>
            <Col span={18}>
              <Form.Item>
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 8,
                    paddingLeft: 81,
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ flexShrink: 0 }}>
                      <span style={{ color: 'red' }}>*</span>每满：
                    </span>
                    <Form.Item
                      noStyle
                      name="threshold"
                      rules={[{ required: true, message: '请输入！' }]}
                    >
                      <InputNumber min={0} placeholder="请输入" />
                    </Form.Item>
                    元，
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ flexShrink: 0 }}>可抵扣：</span>
                    <Form.Item
                      noStyle
                      name="deduct"
                      rules={[{ required: true, message: '请输入' }]}
                    >
                      <InputNumber min={0} placeholder="请输入" />
                    </Form.Item>
                    金币
                  </div>
                </div>
                <div style={{ paddingLeft: 130, color: '#9c9e9c', marginTop: 4 }}>
                  不可输入小于等于0的数
                </div>
              </Form.Item>
            </Col>
            <Col span={18}>
              <Form.Item
                label="最高抵扣（金币）"
                name="maxDeduct"
                rules={[{ required: true, message: '请输入!' }]}
              >
                <InputNumber min={0} placeholder='请输入'/>
              </Form.Item>
            </Col>
            <Col span={18}>
              <Form.Item label="抵扣规则说明" rules={[{ required: false, message: '请输入!' }]}>
                <div style={{ position: 'relative', marginTop: '-15px', width: 600 }}>
                  <Upload showUploadList={false} accept={'image/*'} {...uploadProps(1)}>
                    <div className="zxc" />
                  </Upload>
                  <CKEditor
                    ref={(ckeditor) => {
                      this.ckeditor = ckeditor;
                    }}
                    value={this.state.remark}
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
                      height: 250,
                      uploadUrl: '/home/media/upload',
                      removeDialogTabs: 'image:advanced;link:advanced',
                    }}
                    onChange={this.updateContent}
                  />
                </div>
              </Form.Item>
            </Col>
          </Row>
          <Col span={18} offset={2}>
            <Form.Item>
              {type != 'info' && (
                <>
                  <Button
                    onClick={() => {
                      type == 'edit' ? this.getData() : history.goBack();
                    }}
                  >
                    取消
                  </Button>
                  <Button className="mL15" type="primary" htmlType="submit">
                    保存
                  </Button>
                </>
              )}
            </Form.Item>
          </Col>
        </Form>
      </Spin>
    );
  }
}

export default connect()(CoinUsageRules);
