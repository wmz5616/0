import React from 'react';
import {
  MobileOutlined,
  SafetyCertificateOutlined,
  CheckCircleOutlined,
  LockOutlined,
} from '@ant-design/icons';
import { Form, Input, Button, Row, Col, Radio, message } from 'antd';
import { connect, history } from 'umi';
import styles from '../User/login/index.less';

class Forget extends React.Component {
  state = {
    type: 'PHONE',
    timer: 60,
  };

  formRef = React.createRef();

  onFinish = (values) => {
    console.log('Success:', values);
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        phone: values.phone,
        sms_code: values.sms_code,
        password: values.xxx,
        repassword: values.xxxx,
      },
      url: `/api/admin/account/forget_pwd`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 200) {
          message.success(res.message);
          history.push('/user/login');
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });

    history.push('ChangePassword');
  };

  onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo);
  };

  /**
   * 发送验证码倒计时
   */
  sendCode = () => {
    const { timer, type } = this.state;
    if (this.formRef.current.getFieldValue('phone')) {
      // if (timer === 60) {
      //   this.sendMsg();
      // }
      const siv = setInterval(() => {
        this.setState(
          {
            // eslint-disable-next-line react/no-access-state-in-setstate
            timer: this.state.timer - 1,
            loading: true,
          },
          () => {
            if (this.state.timer <= 0) {
              clearInterval(siv);
              this.setState({
                loading: false,
                timer: 60,
              });
            }
          },
        );
      }, 1000);
    } else {
      message.success('请填写完整信息');
    }
  };

  sendMsg = () => {
    const { dispatch } = this.props;

    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        phone: this.formRef.current.getFieldValue('phone'),
      },
      url: `/api/admin/account/send_code`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 200) {
          this.sendCode(); //获取接口成功执行计数器函数
          message.success(res.message);
        } else {
          message.info(res.message);
        }
      },
    });
  };

  radioChange = (e) => {
    this.setState({
      type: e.target.value,
    });
  };

  render() {
    const { loading, type } = this.state;
    const layout = {
      labelCol: {
        span: 0,
      },
      wrapperCol: {
        span: 24,
      },
    };
    const tailLayout = {
      wrapperCol: {
        offset: 0,
        span: 24,
      },
    };
    return (
      <div className={styles.container}>
        <Row>
          <Col span={12}>
            <img alt="sideImg" src={require('@/assets/images/loginpic.png')} width="100%" />
          </Col>
          <Col span={12}>
            <div className={styles.forgetForm}>
              <div className={styles.mainTitle}>
                {this.props.name}
                <span className={styles.version}>v1.0</span>
              </div>
              <div className={styles.foundTitle}>找回密码</div>
              <Form
                ref={this.formRef}
                colon={false}
                className={styles.formStyle}
                {...layout}
                // name="basic"
                onFinish={this.onFinish}
                onFinishFailed={this.onFinishFailed}
              >
                {/* <Form.Item
                labelCol={{span:4}}
                className='forgetAccount'
                  name="account"
                  rules={[
                    {
                      required: true,
                      message: `请输入`
                    },
                  ]}
                >
                  <Input
                    placeholder={`请输入账号`}
                    className={styles.forgetInput}
                    prefix={<UserOutlined />}
                  />
                </Form.Item> */}

                <Form.Item
                  name="phone"
                  rules={[
                    {
                      required: true,
                      message: `请输入绑定的手机号码!`,
                    },
                  ]}
                >
                  <Input
                    placeholder={`请输入绑定的手机号码`}
                    className={styles.forgetInput}
                    prefix={<MobileOutlined />}
                  />
                </Form.Item>
                <Form.Item
                  name="sms_code"
                  rules={[
                    {
                      required: true,
                      message: '请输入验证码!',
                    },
                  ]}
                >
                  <Input
                    placeholder="请输入验证码"
                    suffix={
                      <Button disabled={loading} onClick={this.sendMsg} className={styles.getCode}>
                        {loading ? `重新发送（${this.state.timer}）` : '获得验证码'}
                      </Button>
                    }
                    className={styles.forgetInput}
                    prefix={<SafetyCertificateOutlined />}
                  />
                </Form.Item>

                <Form.Item
                  name="xxx"
                  rules={[
                    {
                      required: true,
                      message: '请输入新密码！',
                    },
                  ]}
                >
                  <Input
                    type="password"
                    placeholder="请输入新密码"
                    className={styles.forgetInput}
                    prefix={<LockOutlined />}
                  />
                </Form.Item>
                <Form.Item
                  name="xxxx"
                  rules={[
                    {
                      required: true,
                      message: '请确认新密码!',
                    },
                  ]}
                >
                  <Input
                    placeholder="请确认新密码"
                    type="password"
                    className={styles.forgetInput}
                    prefix={<LockOutlined />}
                  />
                </Form.Item>
                {/* <div className={styles.tipText}>8-20个字符，必须同时包含字母和数字。</div> */}

                <Form.Item {...tailLayout}>
                  <Button type="primary" htmlType="submit" className={styles.forgetBtn}>
                    重置密码
                  </Button>
                  <Button
                    onClick={() => {
                      history.goBack();
                    }}
                    className={styles.returnLogin}
                  >
                    返回登录
                  </Button>
                </Form.Item>
              </Form>
            </div>
          </Col>
        </Row>
      </div>
    );
  }
}

export default connect(({ global, settings }) => ({
  collapsed: global.collapsed,
  settings,
  name: global.name,
}))(Forget);
