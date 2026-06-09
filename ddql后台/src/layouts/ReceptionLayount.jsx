import React from 'react';
import styles from './ReceptionLayount.less';
import { Row, Col, Avatar, Menu, Dropdown, message } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { getToken } from '@/utils/authority';
import { history, connect, Redirect } from 'umi';

class ReceptionLayount extends React.Component {

    state = {
        memuFocus: '',
        memuFocus: window.location.pathname == '/reception/childrenTaskDetail' || window.location.pathname == '/reception/fillRecordDetail' || window.location.pathname=='/reception/formFilling'? 'form' : 'home',
        overlayStyle: undefined
    }

    componentDidMount() {
        this.setState({
            overlayStyle: Number(document.getElementById('rightContent').clientWidth) >= 83 ? Number(document.getElementById('rightContent').clientWidth) : 83
        }, () => {
            setTimeout(() => {
                this.setState({
                    overlayStyle: document && document.getElementById('rightContent') && Number(document.getElementById('rightContent').clientWidth) >= 83 ? Number(document.getElementById('rightContent').clientWidth) : 83
                })
            }, 1000)
        })
        const pathname = window.location.pathname;
        if (pathname == `home` || pathname == `messAgeCenter`) {
            this.setState({
                memuFocus: 'home'
            })
        }
        else if (pathname == `/formFilling`) {
            this.setState({
                memuFocus: 'form'
            })
        }
        else {
        }
    }

    memuChang = (key) => {
        this.setState({
            memuFocus: key
        }, () => {
            key == `system` ? history.push({ pathname: '/VenueManagement/index' }) : key == `form` ? history.push({ pathname: '/reception/formFilling' }) : history.push({ pathname: '/reception/home' })
        })
    }

    memuMouseOver = (value) => {
        const element = document.getElementById(value);
        element.style.transform = 'scale(1.1)';
        setTimeout(() => { element.style.transform = 'scale(1)' }, 150)
    }

    memuCallBack = (value) => {
        if (value == 'logout') {
            const loading = message.loading('加载中')
            this.props.dispatch({
                type: 'myModel/getSetData',
                url: `/zemcho-table/security/logout`,
                method: 'POST',
            }).then(res => {
                if (res && res.code === 10000) {
                    loading()
                    window.localStorage.clear()
                    window.location.href = '/user/login'
                }
                else {
                    // message.error(res.msg)
                    window.localStorage.clear()
                    window.location.href = '/user/login'
                }
            })
        }
    }

    render() {
        const menu = (
            <Menu>
                <Menu.Item key='1' onClick={() => this.memuCallBack('materialData')}>
                    <a>
                        个人资料
              </a>
                </Menu.Item>
                <Menu.Item key='2' onClick={() => this.memuCallBack('personalSetting')}>
                    <a>
                        个人设置
              </a>
                </Menu.Item>
                <Menu.Item key="3" onClick={() => this.memuCallBack('logout')}>
                    <a>
                        注销登录
              </a>
                </Menu.Item>
            </Menu>
        )
        const headStyle = {
            height: 60,
            display: 'flex',
            alignItems: 'center',
            cursor: 'pointer',
            paddingLeft: '5.5%'
        }
        const memuData = [
            {
                key: 'home',
                name: '首页',
                style: { width: 120 }
            },
            {
                key: 'form',
                name: '表单填报',
                style: { width: 120 }

            },
            {
                key: 'system',
                name: '系统管理',
                style: { width: 120 }
            },
        ]
        const currentUser = this.props.currentUser || {}
        return (<div>
            <div className={styles.naVigaTion}>
                <div className={styles.headMenuStyle}>
                    <Row>
                        <Col span={8}><div className={styles.headLogo} onClick={() => history.push('/reception/home')}>
                            <img src={require('@/assets/images/homeLogo.png')} /></div>
                        </Col>
                        <Col span={10}>
                            <div style={{ ...headStyle }}>
                                {memuData.map(res => (
                                    <span
                                        key={res.key}
                                        onMouseOver={() => this.memuMouseOver(res.key)}
                                        onMouseOut={this.memuMouseOut}
                                        id={res.key}
                                        onClick={() => this.memuChang(res.key)}
                                        className={styles.memuTitle}
                                        style={{ color: this.state.memuFocus == res.key ? '#3484f5' : '#fff', ...res.style, textAlign: 'center' }}>
                                        {res.name}
                                    </span>
                                ))}
                            </div>
                        </Col>
                        <Col span={6}>
                            <div id='rightContent' className={styles.rightContent}>
                                {getToken() ? (
                                    <Dropdown
                                        overlay={menu}
                                        placement="bottomCenter"
                                        onClick={this.dropdownCallBack}
                                        overlayStyle={{ width: this.state.overlayStyle, paddingTop: 10 }}>
                                        <div style={{ cursor: 'pointer' }}>
                                            <Avatar icon={<img src={require('@/assets/images/suweyLogo.png')} />}
                                            />
                                            <span style={{ color: '#fff', paddingLeft: 15 }}>{currentUser.nickName}
                                            </span>
                                        </div>
                                    </Dropdown>
                                ) : (<div onClick={() => history.push({ pathname: '/user/login', query: { isReception: true } })} style={{ color: '#FFF', cursor: 'pointer' }}>登录</div>)}

                            </div>
                        </Col>
                    </Row>
                </div>
            </div>
            {window.location.pathname == `/` && <Redirect to={`/home`} />}
            {this.props.children}
        </div>)
    }
}

export default connect(({ user }) => {
    return { currentUser: user.currentUser }
})(ReceptionLayount);
