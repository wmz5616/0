import { post } from '@/utils/request';
import { CheckCircleTwoTone } from '@ant-design/icons';
import {
  Button,
  Col,
  Form,
  InputNumber,
  message,
  Row,
  Select,
  Spin,
  Switch,
} from 'antd';
import moment from 'moment';
import React from 'react';
import { history } from 'umi';
const { Option } = Select;

class paymentConfiguration extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    isShouKuan: false,
    merchantInfo: {},
  };

  componentDidMount() {
    const { type, id, disabled = false } = this.props;
    this.setState(
      {
        type,
        id: +id,
        disabled,
      },
      () => {
        this.getMerchantList();
      },
    );
  }

  getMerchantList = async () => {
    const res = await post(`/guzhe/merchant/get/list`, {
      searchIntStatus: 1,
      pageNum: 1,
      pageSize: 999,
    });

    if (res && res.code == 10000) {
      this.setState(
        {
          merchantList: res.data.list,
        },
        () => {
          this.state.type != 'add' && this.getData();
        },
      );
    } else {
      message.error(res?.msg);
    }
  };

  getData = async () => {
    this.setState({
      spinning: true,
    });
    const res = await post(`/guzhe/shop/selectById`, {
      searchId: this.state.id,
    });
    this.setState({
      spinning: false,
    });
    if (res && res.code == 10000) {
      const data = res.data?.shop || {};
      this.formRef.current.setFieldsValue({
        searchField6: data.rate,
      });
      this.setState({
        isShouKuan: data.receiptStatus == 1 ? true : false,
        merchantId: data.merchantId || undefined,
        merchantInfo:
          this.state.merchantList?.find((m) => m.id === data.merchantId) || {},
      });
    } else {
      message.error(res?.msg);
    }
  };

  onFinish = () => {
    //提交
    this.formRef.current.validateFields().then(async (values) => {
      const params = {
        searchId: this.state.id,
        searchType: this.state.isShouKuan ? 1 : 0,
        searchField1: this.state.merchantId,
        searchField6: +values.searchField6,
      };
      const res = await post(`/guzhe/shop/receipt/status`, params);
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    });
  };

  render() {
    const { disabled, type, merchantInfo } = this.state;
    return (
      <Spin spinning={this.state.spinning}>
        <div style={{ paddingTop: 24, paddingRight: 24 }}>
          <Row align="middle" justify="space-between">
            <Col>
              <h2>{this.props.shopName}-收款配置</h2>
            </Col>
            <Col>
              <Button onClick={() => history.back()}>返回</Button>
            </Col>
          </Row>
          <Form
            ref={this.formRef}
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
              <Form.Item
                noStyle
                name="searchField6"
                rules={[{ required: true, message: '请输入！' }]}
              >
                <InputNumber
                  stringMode
                  min={0}
                  max={100}
                  placeholder="请输入"
                />
              </Form.Item>
              <div style={{ color: '#ccc' }}>
                不可输入小于0或大于100的数，未含通莞平台手续费3‰
              </div>
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
                  <div
                    style={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: 20,
                      paddingLeft: 30,
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span
                        style={{
                          textAlign: 'right',
                          width: 132,
                          flexShrink: 0,
                        }}
                      >
                        关联商户：
                      </span>
                      <Select
                        placeholder="请选择需要关联的商户"
                        value={this.state.merchantId}
                        style={{ minWidth: 220 }}
                        disabled={disabled}
                        onChange={(e, a) => {
                          this.setState({
                            merchantId: e,
                            merchantInfo: a.title,
                          });
                        }}
                      >
                        {this.state.merchantList?.map((sa) => (
                          <Option title={sa} value={sa.id}>
                            {sa.merchantName}
                          </Option>
                        ))}
                      </Select>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span style={{ textAlign: 'right', width: 132 }}>
                        商户名称：
                      </span>
                      <div>{merchantInfo && merchantInfo.merchantName}</div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span style={{ textAlign: 'right', width: 132 }}>
                        开通事件：
                      </span>
                      <div>
                        {merchantInfo.createTime
                          ? moment(merchantInfo.createTime).format(
                              'YYYY年MM月DD日 HH:mm:ss',
                            )
                          : ''}
                      </div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span style={{ textAlign: 'right', width: 132 }}>
                        分账银行账户：
                      </span>
                      <div style={{ fontSize: 18 }}>
                        {merchantInfo.accCardNo
                          ? merchantInfo.accCardNo.replace(
                              /(\d{4}|\d{3})/g,
                              '$1 ',
                            )
                          : ''}
                      </div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span style={{ textAlign: 'right', width: 132 }}>
                        账户名：
                      </span>
                      <div>{merchantInfo.accName || ''}</div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span style={{ textAlign: 'right', width: 132 }}>
                        开户行：
                      </span>
                      <div>{merchantInfo.bankName || ''}</div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                      <span style={{ textAlign: 'right', width: 132 }}>
                        商户账户状态：
                      </span>
                      <div>
                        <CheckCircleTwoTone
                          twoToneColor="#52c41a"
                          style={{ marginRight: 5 }}
                        />
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
                      this.state.edit ? this.getData() : history.back();
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
        </div>
      </Spin>
    );
  }
}

export default paymentConfiguration;
