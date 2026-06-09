import React from 'react';
import { message, Select, DatePicker, Tabs, Spin } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import { thisExpression } from '@babel/types';
// import { setToken } from '@/utils/authority';
import BasicInformation from './components/basicInformation';
import ServiceManagement from './components/ServiceManagement'; //服务项目管理
import Hairstylist from './components/Hairstylist'; //发型师管理
import Collection from './components/Collection'; //收款配置

const { Option } = Select;
const { RangePicker } = DatePicker;
// 场所详情

class Login extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    info: {},
    xxx: false,
    disableds: false,
    callbacks:
      this.props &&
      this.props.location &&
      this.props.location.query &&
      this.props.location.query.callbacks
        ? this.props.location.query.callbacks
        : '1',
  };

  // const { id,callbacks,edit } = this.props.location.query;

  componentDidMount() {
    const { edit, id, info: queryInfo } = this.props.location.query || {};
    const { info: stateInfo } = this.props.location.state || {};
    const info = stateInfo || (queryInfo ? JSON.parse(queryInfo) : {});
    
    console.log(info);
    // this.getData();
    this.setState(
      {
        edit: edit,
        info: info,
        id,
      },
      () => {
        this.setState({ xxx: true });
      },
    );
  }

  callback = (key) => {
    // this.getData();
    this.setState({
      callbacks: key,
      disabledss: true,
    });
    // this.setState({
    //   xxx:false
    // },()=>{
    //   this.setState({
    //     xxx:true
    //   })
    // })
  };

  render() {
    console.log(this.state.disableds);
    const { name } = this.props.location.query;

    const routes = [
      {
        path: '/',
        breadcrumbName: '首页',
      },
      {
        breadcrumbName: '团体管理',
      },
      {
        breadcrumbName: `${name ? name : '基本信息'}`,
      },
    ];

    return (
      <Spin spinning={this.state.spinning}>
        <PageContainer
          header={{
            title: ``,
            breadcrumb: {
              itemRender: this.itemRender,
              routes,
            },
          }}
        >
          <div className="yyy" style={{ backgroundColor: '#fff' }}>
            <Tabs
              className="shopTbas"
              tabPosition="left"
              defaultActiveKey={this.state.callbacks}
              onChange={this.callback}
            >
              <Tabs.TabPane tab="基本信息" key="1">
                {this.state.xxx && this.state.callbacks == 1 && (
                  <BasicInformation
                    info={this.state.info}
                    edit={this.state.edit}
                    id={this.state.id}
                    onTypeChange={(type) => {
                      this.setState({
                        info: { ...this.state.info, type }
                      });
                    }}
                    onInfoUpdate={(newInfo) => {
                      this.setState({
                        info: newInfo
                      });
                    }}
                  />
                )}
              </Tabs.TabPane>
              <Tabs.TabPane tab="成员管理" key="2" disabled={this.state.edit != 1}>
                {this.state.xxx && this.state.callbacks == 2 && (
                  <ServiceManagement
                    info={this.state.info}
                    edit={this.state.edit}
                    id={this.state.id}
                    getData={this.getDatas}
                  />
                )}
              </Tabs.TabPane>
              {(this.state.info.type == 0 || this.state.info.type == 1) && (
                <Tabs.TabPane tab="资质认证" key="3" disabled={this.state.edit != 1}>
                  {this.state.xxx && this.state.callbacks == 3 && (
                    <Hairstylist
                      info={this.state.info}
                      edit={this.state.edit}
                      id={this.state.id}
                      callbacks={this.state.callbacks}
                    />
                  )}
                </Tabs.TabPane>
              )}

              <Tabs.TabPane tab="充值记录" key="4" disabled={this.state.edit != 1}>
                {this.state.xxx && this.state.callbacks == 4 && (
                  <Collection
                    info={this.state.info}
                    edit={this.state.edit}
                    id={this.state.id}
                    getData={this.getDatas}
                  />
                )}
              </Tabs.TabPane>
            </Tabs>
          </div>
        </PageContainer>
      </Spin>
    );
  }
}

export default connect()(Login);
