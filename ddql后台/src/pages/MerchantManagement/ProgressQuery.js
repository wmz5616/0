import React from 'react';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Table,
  Select,
  Radio,
  DatePicker,
  Switch,
  Steps,
  Checkbox,
  Result,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import XX from './components/xx';
import {
  DownOutlined,
  UpOutlined,
  CheckCircleTwoTone,
  CloseCircleOutlined,
  InfoCircleOutlined,
  CheckCircleFilled,
  CloseCircleFilled,
} from '@ant-design/icons';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
import moment from 'moment';
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
    const { stadium_id, detail, name, application_no, application_status, created_at } =
      this.props.location.query;
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

          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', margin: '60px 0 40px 0' }}>
            <div style={{ marginRight: 20 }}>
              {xx.applicationStatus == 'REVIEW_BACK' ? (
                <CloseCircleFilled style={{ fontSize: 72, color: '#ff4d4f' }} />
              ) : (
                <CheckCircleFilled style={{ fontSize: 72, color: '#52c41a' }} />
              )}
            </div>
            <div>
              <div style={{ fontSize: 24, fontWeight: 500, color: 'rgba(0, 0, 0, 0.85)', marginBottom: 8 }}>
                {(xx.applicationStatus == 'REVIEWING' || xx.applicationStatus == 'AGREEMENT_SIGNING' || xx.applicationStatus == 'BUSINESS_OPENING') && '进件申请提交成功，正在等待开通中'}
                {xx.applicationStatus == 'REVIEW_BACK' && '进件申请被驳回'}
                {xx.applicationStatus == 'COMPLETED' && '进件申请已通过审核'}
              </div>
              <div style={{ fontSize: 14, color: 'rgba(0, 0, 0, 0.45)' }}>
                {(xx.applicationStatus == 'REVIEWING' || xx.applicationStatus == 'AGREEMENT_SIGNING' || xx.applicationStatus == 'BUSINESS_OPENING') && '预计一个工作日内有审批结果'}
                {xx.applicationStatus == 'REVIEW_BACK' && '请核对并修改进件信息后，再重新提交。'}
                {xx.applicationStatus == 'COMPLETED' && '如有证件信息过期，请联系通莞金服进行证件更新'}
              </div>
            </div>
          </div>

          <div
            style={{
              backgroundColor: '#fafafa',
              width: 600,
              margin: '0 auto',
              padding: '40px 20px',
              fontSize: 14,
              position: 'relative'
            }}
          >
            <div style={{ display: 'flex', marginBottom: 20 }}>
              <div style={{ width: 140, textAlign: 'right', color: '#333' }}>商户名称：</div>
              <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>{xx.merchantName}</div>
            </div>
            <div style={{ display: 'flex', marginBottom: 20 }}>
              <div style={{ width: 140, textAlign: 'right', color: '#333' }}>进件申请单编号：</div>
              <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>
                {xx.applicationNo}
                <Link to={`MerchantDetails?stadium_id=${stadium_id}&detail=${detail}`}>
                  <a style={{ marginLeft: 15, color: '#1890ff' }}>查看详情&gt;</a>
                </Link>
              </div>
            </div>
            <div style={{ display: 'flex', marginBottom: 20 }}>
              <div style={{ width: 140, textAlign: 'right', color: '#333' }}>提交时间：</div>
              <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>{xx.createTime}</div>
            </div>
            <div style={{ display: 'flex', marginBottom: xx.applicationStatus == 'REVIEW_BACK' ? 20 : 0 }}>
              <div style={{ width: 140, textAlign: 'right', color: '#333' }}>申请状态：</div>
              <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>
                {xx.applicationStatus == 'REVIEWING' && (
                  <span>
                    <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} /> 申请审核中
                  </span>
                )}
                {xx.applicationStatus == 'REVIEW_BACK' && (
                  <span>
                    <CloseCircleOutlined style={{ color: 'red', marginRight: 5 }} /> 申请已驳回{xx.auditOpinion && ` (${xx.auditOpinion})`}
                  </span>
                )}
                {xx.applicationStatus == 'AGREEMENT_SIGNING' && (
                  <span>
                    <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} /> 协议待签署
                  </span>
                )}
                {xx.applicationStatus == 'BUSINESS_OPENING' && (
                  <span>
                    <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} /> 业务开通中
                  </span>
                )}
                {xx.applicationStatus == 'COMPLETED' && (
                  <span>
                    <CheckCircleTwoTone twoToneColor="#52c41a" style={{ marginRight: 5 }} /> 申请已完成{xx.auditOpinion && ` (${xx.auditOpinion})`}
                  </span>
                )}
              </div>
            </div>
            {xx.applicationStatus == 'REVIEW_BACK' && (
              <div style={{ display: 'flex' }}>
                <div style={{ width: 140 }}></div>
                <div style={{ flex: 1, paddingLeft: 8 }}>
                  <Link to={`UpdateMerchants?stadium_id=${stadium_id}&detail=${detail}&id=${xx.id}`}>
                    <Button type="primary">修改进件信息</Button>
                  </Link>
                </div>
              </div>
            )}
          </div>
        </div>
      </PageContainer>
    );
  }
}

export default connect()(NoticeNotice);
