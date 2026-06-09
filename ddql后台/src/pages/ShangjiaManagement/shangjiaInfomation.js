import React from 'react';
import { message, Select, DatePicker, Tabs, Spin } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
// import { setToken } from '@/utils/authority';
import BasicInformation from './components/basicInformation';
import CoinUsageRules from './components/coinUsageRules';
import QualificationCertification from './components/qualificationCertification';
import PaymentConfiguration from './components/paymentConfiguration';
import ContractPhotos from './components/contractPhotos';

class shangjiaInfomation extends React.Component {
  formRef = React.createRef();
  state = {
    pageNum: 1,
    list: [],
    circleList: [],
    callbacks: '1',
    xxx: false,
  };

  componentDidMount() {
    const { type, id, callbacks, auditId = undefined, qualificationCert, shopName = '' } = this.props.location.state;
    this.setState(
      {
        type,
        id,
        auditId,
        callbacks: callbacks ? callbacks : '1',
        shopName,
        disabled: type == 'info' ? true : false,
        qualificationCert: type == 'review' ? qualificationCert : 1,
      },
      () => {
        // 保证父组件的state更新完成后再加载子组件
        this.setState({ xxx: true });
        this.getAdminList(1);
      },
    );
  }

  getAdminList = (e) => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        pageSize: 30,
        pageNum: e,
        searchIntStatus: 1,
      },
      url: `/ddql/common/business/circle/lists`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            circleList: res.data,
          });
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  callback = (key) => {
    this.setState({
      callbacks: key,
    });
  };

  render() {
    const { name } = this.state;

    const routes = [
      {
        path: '/',
        breadcrumbName: '首页',
      },
      {
        breadcrumbName: '团体管理',
      },
      {
        breadcrumbName: `${name ? name : '新建门店'}`,
      },
    ];

    return (
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
              {this.state.xxx && this.state.callbacks == '1' && (
                <BasicInformation
                  type={this.state.type}
                  id={this.state.id}
                  auditId={this.state.auditId}
                  circleList={this.state.circleList}
                  disabled={this.state.disabled}
                  editShopName={(name) => this.setState({ shopName: name })}
                />
              )}
            </Tabs.TabPane>
            {this.state.type != 'review' && this.state.type != 'info' && (
              <Tabs.TabPane tab="用币规则" key="2">
                {this.state.xxx && this.state.callbacks == '2' && (
                  <CoinUsageRules
                    type={this.state.type}
                    id={this.state.id}
                    disabled={this.state.disabled}
                    shopName={this.state.shopName}
                  />
                )}
              </Tabs.TabPane>
            )}
            {this.state.qualificationCert && <Tabs.TabPane tab="资质认证" key="3">
              {this.state.xxx && this.state.callbacks == '3' && (
                <QualificationCertification
                  type={this.state.type}
                  id={this.state.id}
                  disabled={this.state.disabled}
                />
              )}
            </Tabs.TabPane>}
            {this.state.type != 'review' && this.state.type != 'info' && (
              <Tabs.TabPane tab="收款配置" key="4">
                {this.state.xxx && this.state.callbacks == '4' && (
                  <PaymentConfiguration
                    type={this.state.type}
                    id={this.state.id}
                    disabled={this.state.disabled}
                    shopName={this.state.shopName}
                  />
                )}
              </Tabs.TabPane>
            )}
            {this.state.type != 'review' && this.state.type != 'info' && (
              <Tabs.TabPane tab="合同照片" key="5">
                {this.state.xxx && this.state.callbacks == '5' && (
                  <ContractPhotos
                    type={this.state.type}
                    id={this.state.id}
                    disabled={this.state.disabled}
                    shopName={this.state.shopName}
                  />
                )}
              </Tabs.TabPane>
            )}
          </Tabs>
        </div>
      </PageContainer>
    );
  }
}

export default connect()(shangjiaInfomation);
