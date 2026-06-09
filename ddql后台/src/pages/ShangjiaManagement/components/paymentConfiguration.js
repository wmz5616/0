import React from 'react';
import { PlusOutlined, CheckCircleTwoTone, RightCircleFilled } from '@ant-design/icons';
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
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
const { Option } = Select;

class paymentConfiguration extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    isShouKuan: false,
  };

  componentDidMount() {
    const { type, id, disabled = false } = this.props;
    this.setState(
      {
        type,
        id,
        disabled,
      },
      () => {
        // this.getMerchantList();
        type != 'add' && this.getData();
      },
    );
  }

  // getMerchantList = async () => {
  //   const res = await post(`/guzhe/merchant/get/list`, {
  //     searchIntStatus: 1,
  //     pageNum: 1,
  //     pageSize: 999,
  //   });

  //   if (res && res.code == 10000) {
  //     this.setState({
  //       merchantList: res.data.list,
  //     });
  //   } else {
  //     message.error(res?.msg);
  //   }
  // }

  onFinish = () => {
    this.formRef.current.validateFields().then(async (values) => {
      const params = {
        searchId: this.state.id,
        searchType: this.state.isShouKuan ? 1 : 0,
        searchField1: this.state.merchantId,
        searchField2: +values.searchField2,
      };
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: params,
        url: `/ddql/business/shop/receipt/status`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code == 10000) {
            message.success(res.msg);
            this.getData()
          } else {
            message.error(res?.msg);
          }
        },
      })
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
      url: `/ddql/business/shop/selectById`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code === 10000) {
          const data = res.data?.shop || {};
          this.formRef.current.setFieldsValue({
            searchField2: data.rate,
          });
          this.setState({
            isShouKuan: data.receiptStatus == 1 ? true : false,
            merchantId: data.merchantId || undefined,
            merchantInfo: this.state.merchantList?.find((m) => m.id === data.merchantId) || {},
          })
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  render() {
    const { disabled, type } = this.state;
    return (
      <Spin spinning={this.state.spinning}>
        <Row align="middle" justify="space-between">
          <Col span={4}>
            <h2>收款配置</h2>
          </Col>
          <Col span={2}>
            <Button onClick={() => history.goBack()}>返回</Button>
          </Col>
        </Row>
        <Form
          ref={this.formRef}
          labelCol={{
            span: 2,
          }}
          wrapperCol={{
            span: 10,
          }}
          onFinish={this.onFinish}
          initialValues={{
            remember: true,
          }}
          autoComplete="off"
          disabled={disabled}
        >
          <Form.Item
            label={
              <span>
                <text style={{ color: 'red' }}>*</text>抽成比例（%）
              </span>
            }
            style={{ display: 'flex', flexDirection: 'column' }}
          >
            <Form.Item noStyle name="searchField2" rules={[{ required: true, message: '请输入！' }]}>
              <InputNumber min={0} max={100} placeholder='请输入' />
            </Form.Item>
            <div style={{ color: '#ccc' }}>不可输入小于0或大于100的数</div>
          </Form.Item>
          <Form.Item>
            <div
              style={{
                padding: '20px',
                boxSizing: 'border-box',
                border: '1px solid #e9e9e9',
                backgroundColor: '#fafafa',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                borderRadius: '4px',
              }}
            >
              <div>
                <h3 style={{ marginTop: 0 }}>门店收款功能</h3>
                <div style={{ color: '#ccc' }}>
                  开启门店收款功能则支持该门店通过小程序端进行收费预订项目。
                </div>
              </div>
              <Switch
                checkedChildren="开"
                unCheckedChildren="关"
                checked={this.state.isShouKuan}
                onChange={(e) => {
                  this.setState({
                    isShouKuan: e,
                  });
                }}
              />
            </div>
          </Form.Item>
          {this.state.isShouKuan && (
            <Form.Item>
              <div
                style={{
                  padding: '20px',
                  boxSizing: 'border-box',
                  border: '1px solid #e9e9e9',
                  backgroundColor: '#fafafa',
                  display: 'flex',
                  flexDirection: 'column',
                  borderRadius: '4px',
                  gap: 20,
                }}
              >
                <h3 style={{ marginTop: 0 }}>商户基本信息</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 20, paddingLeft: 30 }}>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ textAlign: 'right', width: 132, flexShrink: 0 }}>
                      关联商户：
                    </span>
                    <Select
                      placeholder="请选择需要关联的商户"
                      style={{ minWidth: 220 }}
                      disabled={disabled}
                    >
                      <Option>11111</Option>
                    </Select>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ textAlign: 'right', width: 132 }}>商户信息：</span>
                    <Link>查看详情</Link>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ textAlign: 'right', width: 132 }}>开通事件：</span>
                    <div>2023年4月18日 23:08:00</div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ textAlign: 'right', width: 132 }}>分账银行账户：</span>
                    <div>2010 0504 1910 0307 987</div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ textAlign: 'right', width: 132 }}>账户名：</span>
                    <div>东莞市蓝睿网络科技有限公司</div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ textAlign: 'right', width: 132 }}>开户行：</span>
                    <div>中国工商银行东莞松山湖支行</div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ textAlign: 'right', width: 132 }}>商户账户状态：</span>
                    <div>
                      <CheckCircleTwoTone twoToneColor="#52c41a" style={{ marginRight: 5 }} />
                      已开通
                    </div>
                  </div>
                </div>
              </div>
            </Form.Item>
          )}
          <Form.Item>
            {type != 'info' && (
              <>
                <Button
                  onClick={() => {
                    this.state.edit ? this.getData() : history.goBack();
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
        </Form>
      </Spin>
    );
  }
}

export default connect()(paymentConfiguration);
