import React from 'react';
import ReactSimpleVerify from '../../components/ReactSimpleVerify';
import { UserOutlined, LockOutlined, FormOutlined } from '@ant-design/icons';
import { Form, Input, Button, Row, Col, message } from 'antd';
import { history, connect } from 'umi';
import { setToken, UserInformation, setAuthority } from '@/utils/authority';
import Captcha from 'react-captcha-code';

// import { setAuthority } from '@/utils/authority';

import styles from './index.less';
import CryptoJS from 'crypto-js';

function encryptDES3(message, key, iv) {
  // 将消息和密钥转换为字节数组
  const messageBytes = CryptoJS.enc.Utf8.parse(message);
  const keyBytes = CryptoJS.enc.Utf8.parse(key);

  // 执行加密（使用DES3算法，CBC模式）
  const encrypted = CryptoJS.TripleDES.encrypt(messageBytes, keyBytes, {
    iv: CryptoJS.enc.Utf8.parse(iv),
    mode: CryptoJS.mode.CBC,
  });

  // 将加密结果转换为字符串
  const ciphertext = encrypted.toString();

  return ciphertext;
}

class Login extends React.Component {
  state = {
    sliderSuc: false,
    loading: false,
  };

  componentDidMount() {
    if (
      navigator.userAgent.match(/Android/i) ||
      navigator.userAgent.match(/webOS/i) ||
      navigator.userAgent.match(/iPhone/i) ||
      navigator.userAgent.match(/iPad/i) ||
      navigator.userAgent.match(/iPod/i) ||
      navigator.userAgent.match(/BlackBerry/i) ||
      navigator.userAgent.match(/Windows Phone/i)
    ) {
      console.log(55555555);
      this.setState({
        modes: true,
      });
      // 移动端适配
    } else {
      this.setState({
        modes: false,
      });
      console.log(66666666666);
    }
  }

  onFinish = (values) => {
    const encryptionKey = 'rPgEVmxhvxaWSdhOTgcnqJrp'; // 密(24位)
    const initializationVector = 62389533; // 初始化向量 (8位)

    const password = encryptDES3(values.password, encryptionKey, initializationVector);
    const text = CryptoJS.TripleDES.encrypt(
      CryptoJS.enc.Utf8.parse(values.password),
      CryptoJS.enc.Utf8.parse('rPgEVmxhvxaWSdhOTgcnqJrp'),
      { mode: CryptoJS.mode.CBC, iv: CryptoJS.enc.Utf8.parse(initializationVector) },
    ).toString();
    const params = {
      account: values.account,
      password: text,
    };
    const query = this.props.location.query || {};
    const { isReception } = query;
    const { sliderSuc, captcha, modes } = this.state;

    if (modes) {
      if (captcha != values.code) {
        message.info('验证码错误');
        return;
      }
    } else {
      if (!sliderSuc) {
        message.info('请完成滑块验证');
        return;
      }
    }

    this.setState({ loading: true }, () => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        url: `/ddql/cas/login`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            setToken(res.data);
            this.setState({ loading: false });
            fetch('/ddql/cas/info', {
              headers: {
                'Content-Type': 'application/json',
                token: res.data,
              },
            })
              .then((response) => response.json())
              .then((ressd) => {
                console.log(ressd.data);
                window.localStorage.setItem('userInfo', JSON.stringify(ressd.data));
                // window.location.href = '/';
                //获取单位下拉列表
                if (ressd.data.id == 1) {
                  setAuthority([
                    1, 2, 13, 111, 112, 113, 144, 146, 147, 14, 15, 16, 17, 18, 19, 20, 3, 25, 28,
                    29, 26, 30, 114, 31, 32, 33, 27, 34, 115, 35, 36, 37, 38, 4, 89, 90, 91, 92, 93,
                    94, 95, 96, 97, 5, 39, 116, 117, 118, 40, 41, 42, 43, 44, 6, 68, 69, 119, 145,
                    70, 71, 72, 73, 74, 75, 76, 77, 98, 99, 102, 103, 104, 105, 106, 107, 108, 109,
                    110, 7, 78, 79, 80, 81, 82, 83, 101, 84, 85, 86, 87, 88, 100, 8, 45, 46, 47, 48,
                    49, 9, 21, 120, 121, 122, 123, 124, 125, 22, 23, 24, 10, 126, 127, 128, 129,
                    130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 11, 50,
                    51, 52, 58, 59, 60, 61, 62, 63, 53, 54, 55, 56, 57, 12, 64, 65, 66, 67,
                  ]);
                } else {
                  setAuthority(ressd.data.ruleIds);
                }
                window.location.href = '/welcome';
              });
            // window.location.href = '/allUser'; //判断是从前台登录还是点击前台系统管理登录
          } else {
            message.error(res.msg);
            this.setState({ loading: false });
          }
        },
      });
    });
  };

  success = () => {
    this.setState({
      sliderSuc: true,
    });
  };

  handleClick = (captcha) => {
    this.setState({
      captcha,
    });
    // console.log(captcha);
  };

  render() {
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
            <img
              alt="sideImg"
              src={require('@/assets/images/loginpic.png')}
              width="100%"
              style={{ heigth: '100%' }}
            />
          </Col>
          <Col span={12} style={{ position: 'relative', height: 553 }}>
            <div className={styles.loginForm}>
              <div className={styles.mainTitle}>
                {this.props.name}
                <span className={styles.version}>v1.0</span>
              </div>
              <Form
                className={styles.formStyle}
                {...layout}
                // name="basic"
                onFinish={this.onFinish}
                onFinishFailed={this.onFinishFailed}
              >
                <Form.Item
                  name="account"
                  rules={[
                    {
                      required: true,
                      message: '请输入账号!',
                    },
                  ]}
                >
                  <Input
                    placeholder="请输入账号"
                    className={styles.inputStyle}
                    prefix={<UserOutlined />}
                  />
                </Form.Item>
                <Form.Item
                  name="password"
                  rules={[
                    {
                      required: true,
                      message: '请输入登录密码!',
                    },
                  ]}
                >
                  <Input
                    className={styles.inputStyle}
                    prefix={<LockOutlined />}
                    type="password"
                    placeholder="请输入登录密码"
                    // suffix={
                    //   <div
                    //     className={styles.forget}
                    //     onClick={() => {
                    //       history.push('/user/forget');
                    //     }}
                    //   >
                    //     忘记密码？
                    //   </div>
                    // }
                  />
                </Form.Item>

                {this.state.modes ? (
                  <Form.Item
                    name="code"
                    rules={[
                      {
                        required: true,
                        message: '请输入验证码!',
                      },
                    ]}
                  >
                    <Input
                      className={styles.inputStyle}
                      prefix={<FormOutlined />}
                      placeholder="请输入验证码"
                      suffix={
                        <div className={styles.forget}>
                          <Captcha charNum={2} onChange={this.handleClick} />
                        </div>
                      }
                    />
                  </Form.Item>
                ) : (
                  <Form.Item className="mt20">
                    <ReactSimpleVerify // ref="verify"
                      tips="拖动滑块验证"
                      width="324"
                      success={() => this.success()}
                    />
                  </Form.Item>
                )}

                <Form.Item {...tailLayout}>
                  <Button
                    type="primary"
                    htmlType="submit"
                    className={styles.loginBtn}
                    loading={this.state.loading}
                  >
                    登录
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
}))(Login);
