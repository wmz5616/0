import {
  CheckCircleTwoTone,
  CloseCircleOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { DatePicker, Result, Select, Steps } from 'antd';
import React from 'react';
import { history } from 'umi';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 10 },
};

class NoticeNotice extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    xxx: false,
    current: 0,
    licenceUrls: [],
    openAccountLicenceUrls: [],
    IdPhoto: [],
    IdPhotoB: [],
    // operationTypes:'1'
  };

  render() {
    // const { stadium_id, detail, name, application_no, application_status, created_at } =
    //   this.props.location.query;
    const searchParams = new URLSearchParams(history.location?.search || '');
    const detail = searchParams.get('detail');
    const xx = JSON.parse(detail);
    console.log(xx);
    const routes = [
      {
        path: '/',
        breadcrumbName: '首页',
      },
      {
        breadcrumbName: '商户管理',
      },
      {
        breadcrumbName: `新增商户`,
      },
    ];

    return (
      <PageContainer
        header={{
          title: `${xx.merchantName}-申请状态`,
          breadcrumb: {
            itemRender: this.itemRender,
            routes,
          },
        }}
      >
        <div style={{ backgroundColor: '#fff', padding: 24, height: 600 }}>
          <div style={{ maxWidth: 1100, margin: '0 auto', marginTop: 20 }}>
            <Steps current={2}>
              <Steps.Step title="填写进件信息" />
              <Steps.Step title="确认进件信息" />
              <Steps.Step title="完成" />
            </Steps>
          </div>

          <Result
            status={xx.applicationStatus == 'REVIEW_BACK' ? 'error' : 'success'}
            title={
              <>
                {xx.applicationStatus == 'REVIEWING' && '申请审核中'}
                {xx.applicationStatus == 'REVIEW_BACK' && '进件申请被驳回'}

                {xx.applicationStatus == 'COMPLETED' && '进件申请已通过审核'}
              </>
            }
            subTitle={
              <>
                {xx.applicationStatus == 'REVIEWING' &&
                  '预计一个工作日内有审批结果'}
                {xx.applicationStatus == 'REVIEW_BACK' &&
                  '请核对并修改进件信息后，再重新提交。'}

                {xx.applicationStatus == 'COMPLETED' &&
                  '如有证件信息过期，请联系通莞金服进行证件更新'}
              </>
            }
          />
          <div
            style={{
              backgroundColor: '#fafafa',
              width: 550,
              margin: '0 auto',
              padding: 30,
            }}
          >
            <p style={{ marginLeft: 42 }}>商户名称： {xx.merchantName}</p>
            <div style={{display: 'flex', alignItems: 'center', gap: 10,marginBottom:'1em'}}>
              进件申请单编号：{xx.applicationNo}
              <div
                className="clickFont"
                onClick={() => {
                  history.push(
                    `/MerchantManagement/MerchantDetails?detail=${detail}`,
                  );
                }}
              >
                查看详情  
              </div>
              {/* <Link
                to={`MerchantDetails?stadium_id=${stadium_id}&detail=${detail}`}
              >
                <a style={{ marginLeft: 10 }}>查看详情 </a>
              </Link> */}
            </div>
            <p style={{ marginLeft: 42 }}>提交时间：{xx.createTime}</p>
            <p style={{ marginLeft: 42 }}>
              申请状态：
              {xx.applicationStatus == 'REVIEWING' && (
                <span>
                  {' '}
                  <InfoCircleOutlined style={{ color: '#1890ff' }} /> 申请审核中
                </span>
              )}
              {xx.applicationStatus == 'REVIEW_BACK' && (
                <span>
                  {' '}
                  <CloseCircleOutlined style={{ color: 'red' }} /> 申请已驳回
                </span>
              )}
              {xx.applicationStatus == 'AGREEMENT_SIGNING' && (
                <span>
                  {' '}
                  <InfoCircleOutlined style={{ color: '#1890ff' }} /> 协议待签署
                </span>
              )}
              {xx.applicationStatus == 'BUSINESS_OPENING' && (
                <span>
                  {' '}
                  <InfoCircleOutlined style={{ color: '#1890ff' }} /> 业务开通中
                </span>
              )}
              {xx.applicationStatus == 'COMPLETED' && (
                <span>
                  <CheckCircleTwoTone twoToneColor="#52c41a" />{' '}
                  申请已完成（通过）
                </span>
              )}
            </p>
          </div>
        </div>
      </PageContainer>
    );
  }
}

export default NoticeNotice;
