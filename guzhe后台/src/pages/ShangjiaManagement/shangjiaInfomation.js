import React from 'react';
import { message, Select, DatePicker, Tabs, Spin } from 'antd';
import { history } from '@umijs/max';
import { PageContainer } from '@ant-design/pro-layout';
import BasicInformation from './components/basicInformation';
import ProductManagement from './components/productManagement';
import QualificationCertification from './components/qualificationCertification';
import PaymentConfiguration from './components/paymentConfiguration';
import ContractPhotos from './components/contractPhotos';
import { post } from '@/utils/request';

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
    const searchParams = new URLSearchParams(history.location?.search || '');
    const type = searchParams.get('type');
    const callbacks = searchParams.get('callbacks');
    const qualificationCert = searchParams.get('qualificationCert');
    console.log(searchParams.get('id'));
    this.setState(
      {
        type,
        id: searchParams.get('id'),
        auditId: searchParams.get('auditId') || undefined,
        shopName: searchParams.get('shopName') || '',
        callbacks: callbacks ? callbacks : '1',
        disabled: type == 'info' ? true : false,
        qualificationCert: type == 'review' ? qualificationCert : 1,
      },
      () => {
        // 保证父组件的state更新完成后再加载子组件
        this.setState({ xxx: true });
        this.getAdminList();
      },
    );
  }

  getAdminList = async () => {
    const res = await post(`/guzhe/supermarket/select`, {
      pageSize: 999,
      searchType: 1,
    });
    if (res && res.code == 10000) {
      this.setState({
        circleList: res.data?.list || [],
      });
    } else {
      message.error(res?.msg);
    }
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
            <Tabs.TabPane tab="商家信息" key="1">
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
            {/* {this.state.qualificationCert && <Tabs.TabPane tab="资质认证" key="2">
              {this.state.xxx && this.state.callbacks == '2' && (
                <QualificationCertification
                  type={this.state.type}
                  id={this.state.id}
                  disabled={this.state.disabled}
                  auditId={this.state.auditId}
                />
              )}
            </Tabs.TabPane>} */}
            {(this.state.type != 'review' && this.state.type != 'info') && (
              <Tabs.TabPane tab="商品管理" key="3">
                {this.state.xxx && this.state.callbacks == '3' && (
                  <ProductManagement
                    type={this.state.type}
                    id={this.state.id}
                    disabled={this.state.disabled}
                    shopName={this.state.shopName}
                  />
                )}
              </Tabs.TabPane>
            )}
            {this.state.type != 'review' && this.state.type != 'info' && (
              <Tabs.TabPane tab="海报管理" key="4">
                {this.state.xxx && this.state.callbacks == '4' && (
                  <ContractPhotos
                    type={this.state.type}
                    id={this.state.id}
                    disabled={this.state.disabled}
                    isContract={false}
                    shopName={this.state.shopName}
                  />
                )}
              </Tabs.TabPane>
            )}
            {this.state.type != 'review' && this.state.type != 'info' && (
              <Tabs.TabPane tab="收款配置" key="5">
                {this.state.xxx && this.state.callbacks == '5' && (
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
              <Tabs.TabPane tab="合同信息" key="6">
                {this.state.xxx && this.state.callbacks == '6' && (
                  <ContractPhotos
                    type={this.state.type}
                    id={this.state.id}
                    disabled={this.state.disabled}
                    isContract={true}
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

export default shangjiaInfomation;
