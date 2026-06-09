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
  Alert,
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
    Disablesss: true,
    // operationTypes:'1'
  };

  componentDidMount() {
    // this.getData();
  }

  getData = () => {
    //场所
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        limit: 999,
      },
      url: `/api/admin/stadium/lists`,
      method: 'GET',
      myData: (res) => {
        if (res && res.code === 200) {
          console.log(res.data);
          this.setState({
            stadiumList: res.data.lists,
          });
        } else {
          message.error(res.message);
        }
      },
    });
  };

  nextss = (vv) => {
    this.setState(
      {
        Disablesss: false,
      },
      () => {
        this.formRef.current.validateFields().then((values) => {
          this.setState(
            {
              stadium_id: values.stadium_id, //场所id
              operationType: values.operationType,
              merchantBasic: {
                merchantName: values.merchantName, //商户名称
                contactPhone: values.contactPhone, //联系电话
                email: values.email, //邮箱地址
              },

              //负责人身份证信息
              merchantCard: {
                cardName: values.cardName, //身份证名称
                cardNo: values.cardNo, //身份证号码
                cardMobile: values.cardMobile, //手机
                cardBeginDate:
                  values.cardBeginDate &&
                  (values.cardBeginDate.length > 1
                    ? values.cardBeginDate[0].format('YYYY-MM-DD')
                    : values.cardBeginDate.format('YYYY-MM-DD')), //身份证开始时间
                cardEndDate:
                  values.cardBeginDate &&
                  (values.cardBeginDate.length > 1
                    ? values.cardBeginDate[1].format('YYYY-MM-DD')
                    : '长期'), //身份证结束时间
              },

              //门店信息
              merchanStore: {
                storeName: values.storeName, //门店名称
                storeProvince: values.storeCounty && values.storeCounty[0], //省
                storeCity: values.storeCounty && values.storeCounty[1], //市
                storeCounty: values.storeCounty && values.storeCounty[2], //区
                storeAddr: values.storeAddr, //详情地址
              },

              //营业信息（操作类型为1时必填）
              merchanBusiness:
                values.operationType == 1 || true
                  ? {
                      businessName: values.businessName, //主体名称
                      businessNo: values.businessNo, //主题编号
                      mainType: values.mainType, //主体类型
                      legalPerson: values.legalPerson, //法人名称
                      businessProvince: values.businessProvince ? values.businessProvince[0] : '', //营业地址省
                      businessCity: values.businessProvince ? values.businessProvince[1] : '', //营业地址市
                      businessCounty: values.businessProvince ? values.businessProvince[2] : '', //营业地址区
                      businessAddr: values.businessAddr, //营业详细地址
                      businessBeginDate: values.businessBeginDate
                        ? values.businessBeginDate.length > 1
                          ? values.businessBeginDate[0].format('YYYY-MM-DD')
                          : values.businessBeginDate.format('YYYY-MM-DD')
                        : '', //营业开始时间
                      businessEndDate: values.businessBeginDate
                        ? values.businessBeginDate.length > 1
                          ? values.businessBeginDate[1].format('YYYY-MM-DD')
                          : '长期'
                        : '', //营业结束时间
                    }
                  : undefined,
              //账户类型
              acc: {
                accType: values.accType, //账户类型10070=对公账户，10071=法人账户
                accCardNo: values.accCardNo, //银行账号
                accMobile: values.accMobile, //开户预留手机
                accName: values.accName, //账户名
                bank: values.bank, //开户行编号
                bankName: this.state.bankNames, //开户行名字
                province: values.province && values.province[0], //开户地址省
                city: values.province && values.province[1], //开户地址市
                bankLinkNo: values.bankLinkNo, //支行联行号
                bankBranch: this.state.bankBranchs, //支行名称
              },

              licenceUrl: this.state.licenceUrls
                ? this.state.licenceUrls.length > 0 && this.state.licenceUrls[0].url
                : undefined, //营业执照照片
              // parentChannelMerchantNo: values.parentChannelMerchantNo, //主商户编号

              legalLicenceFrontUrl:
                this.state.IdPhoto && this.state.IdPhoto.length > 0 && this.state.IdPhoto[0].url, //身份证正面
              legalLicenceBackUrl:
                this.state.IdPhotoB && this.state.IdPhotoB.length > 0 && this.state.IdPhotoB[0].url, //身份证反面
              openAccountLicenceUrl:
                this.state.openAccountLicenceUrls &&
                this.state.openAccountLicenceUrls.length > 0 &&
                this.state.openAccountLicenceUrls[0].url, //*开户许可证照片
            },
            () => {
              const params = {
                status:vv,
                // stadium_id: this.state.stadium_id, //场所id
                operationType: this.state.operationType,
                ...this.state.merchantBasic,
                ...this.state.merchantCard, //负责人信息
                ...this.state.merchanStore, //门店信息

                ...this.state.acc,

                licenceUrlLocal: this.state.licenceUrls
                  ? this.state.licenceUrls.length > 0 && this.state.licenceUrls[0].url
                    ? this.state.licenceUrls[0].url
                    : ''
                  : '', //营业执照照片
                parentChannelMerchantNo: '10090719235', //执照/证书编号

                legalLicenceFrontUrlLocal:
                  this.state.IdPhoto && this.state.IdPhoto.length > 0 && this.state.IdPhoto[0].url
                    ? this.state.IdPhoto[0].url
                    : '', //身份证正面
                legalLicenceBackUrlLocal:
                  this.state.IdPhotoB && this.state.IdPhotoB.length > 0 && this.state.IdPhotoB[0].url
                    ? this.state.IdPhotoB[0].url
                    : '', //身份证反面
                openAccountLicenceUrlLocal:
                  this.state.openAccountLicenceUrls && this.state.openAccountLicenceUrls.length > 0 &&
                  this.state.openAccountLicenceUrls[0].url
                    ? this.state.openAccountLicenceUrls[0].url
                    : '', //*开户许可证照片
              };
              const merchanBusiness = this.state.merchanBusiness ? this.state.merchanBusiness : {};
              const mergedObj = Object.assign({}, params, merchanBusiness);
              this.props.dispatch({
                type: 'myModel/getSetData',
                payload: {
                  ...mergedObj,
                  // is_draft: 1,
                  // stadium_id: this.state.stadium_id, //场所id
                  // operationType: this.state.operationType,
                  // merchantBasic: this.state.merchantBasic,
                  // merchantCard: this.state.merchantCard, //负责人信息
                  // merchanStore: this.state.merchanStore, //门店信息
                  // merchanBusiness: this.state.merchanBusiness, //营业信息

                  // acc: this.state.acc,

                  // licenceUrl: this.state.licenceUrls
                  //   ? this.state.licenceUrls.length > 0 && this.state.licenceUrls[0].url
                  //   : '', //营业执照照片
                  // parentChannelMerchantNo: '10090719235', //执照/证书编号

                  // legalLicenceFrontUrl: this.state.IdPhoto.length > 0 && this.state.IdPhoto[0].url, //身份证正面
                  // legalLicenceBackUrl: this.state.IdPhotoB.length > 0 && this.state.IdPhotoB[0].url, //身份证反面
                  // openAccountLicenceUrl:
                  //   this.state.openAccountLicenceUrls.length > 0 &&
                  //   this.state.openAccountLicenceUrls[0].url, //*开户许可证照片
                },
                url: `/ddql/merchant/add`,
                method: 'POST',
                myData: (res) => {
                  if (res && res.code === 10000) {
                    message.success(res.msg);
                    this.props.history.goBack();
                  } else {
                    this.setState({
                      Disablesss: true,
                    });
                    message.error(res.msg);
                  }
                },
              });
            },
          );
        });
      },
    );
  };

  next = (vv) => {
    this.formRef.current.validateFields().then((values) => {
      this.setState(
        {
          stadium_id: values.stadium_id, //场所id
          operationType: values.operationType,
          merchantBasic: {
            merchantName: values.merchantName, //商户名称
            contactPhone: values.contactPhone, //联系电话
            email: values.email, //邮箱地址
          },

          //负责人身份证信息
          merchantCard: {
            cardName: values.cardName, //身份证名称
            cardNo: values.cardNo, //身份证号码
            cardMobile: values.cardMobile, //手机
            cardBeginDate:
              values.cardBeginDate.length > 1
                ? values.cardBeginDate[0].format('YYYY-MM-DD')
                : values.cardBeginDate.format('YYYY-MM-DD'), //身份证开始时间
            cardEndDate:
              values.cardBeginDate.length > 1
                ? values.cardBeginDate[1].format('YYYY-MM-DD')
                : '长期', //身份证结束时间
          },

          //门店信息
          merchanStore: {
            storeName: values.storeName, //门店名称
            storeProvince: values.storeCounty[0], //省
            storeCity: values.storeCounty[1], //市
            storeCounty: values.storeCounty[2], //区
            storeAddr: values.storeAddr, //详情地址
          },

          //营业信息（操作类型为1时必填）
          merchanBusiness:
            values.operationType == 1 || true
              ? {
                  businessName: values.businessName, //主体名称
                  businessNo: values.businessNo, //主题编号
                  mainType: values.mainType, //主体类型
                  legalPerson: values.legalPerson, //法人名称
                  businessProvince: values.businessProvince ? values.businessProvince[0] : '', //营业地址省
                  businessCity: values.businessProvince ? values.businessProvince[1] : '', //营业地址市
                  businessCounty: values.businessProvince ? values.businessProvince[2] : '', //营业地址区
                  businessAddr: values.businessAddr, //营业详细地址
                  businessBeginDate: values.businessBeginDate
                    ? values.businessBeginDate.length > 1
                      ? values.businessBeginDate[0].format('YYYY-MM-DD')
                      : values.businessBeginDate.format('YYYY-MM-DD')
                    : '', //营业开始时间
                  businessEndDate: values.businessBeginDate
                    ? values.businessBeginDate.length > 1
                      ? values.businessBeginDate[1].format('YYYY-MM-DD')
                      : '长期'
                    : '', //营业结束时间
                }
              : undefined,
          //账户类型
          acc: {
            accType: values.accType, //账户类型10070=对公账户，10071=法人账户
            accCardNo: values.accCardNo, //银行账号
            accMobile: values.accMobile, //开户预留手机
            accName: values.accName, //账户名
            bank: values.bank, //开户行编号
            bankName: this.state.bankNames, //开户行名字
            province: values.province[0], //开户地址省
            city: values.province[1], //开户地址市
            bankLinkNo: values.bankLinkNo, //支行联行号
            bankBranch: this.state.bankBranchs, //支行名称
          },

          licenceUrl: this.state.licenceUrls
            ? this.state.licenceUrls.length > 0 && this.state.licenceUrls[0].url
            : '', //营业执照照片
          // parentChannelMerchantNo: values.parentChannelMerchantNo, //主商户编号

          legalLicenceFrontUrl:
            this.state.IdPhoto && this.state.IdPhoto.length > 0 && this.state.IdPhoto[0].url
              ? this.state.IdPhoto[0].url
              : '', //身份证正面
          legalLicenceBackUrl:
            this.state.IdPhotoB && this.state.IdPhotoB.length > 0 && this.state.IdPhotoB[0].url
              ? this.state.IdPhotoB[0].url
              : '', //身份证反面
          openAccountLicenceUrl:
            this.state.openAccountLicenceUrls &&
            this.state.openAccountLicenceUrls.length > 0 &&
            this.state.openAccountLicenceUrls[0].url
              ? this.state.openAccountLicenceUrls[0].url
              : '', //*开户许可证照片

          current: this.state.current + 1,
        },
        () => {
          this.setState({
            Disables: this.state.current == 1 ? true : false,
          });
        },
      );
    });
  };

  prev = () => {
    this.setState(
      {
        current: this.state.current - 1,
      },
      () => {
        this.setState({
          Disables: this.state.current == 1 ? true : false,
        });
      },
    );
  };

  submits = (e) => {
    const params = {
      status: e,
      // stadium_id: this.state.stadium_id, //场所id
      operationType: this.state.operationType,
      ...this.state.merchantBasic,
      ...this.state.merchantCard, //负责人信息
      ...this.state.merchanStore, //门店信息

      ...this.state.acc,

      licenceUrlLocal: this.state.licenceUrls
        ? this.state.licenceUrls.length > 0 && this.state.licenceUrls[0].url
          ? this.state.licenceUrls[0].url
          : ''
        : '', //营业执照照片
      parentChannelMerchantNo: '10090719235', //执照/证书编号

      legalLicenceFrontUrlLocal:
        this.state.IdPhoto.length > 0 && this.state.IdPhoto[0].url ? this.state.IdPhoto[0].url : '', //身份证正面
      legalLicenceBackUrlLocal:
        this.state.IdPhotoB.length > 0 && this.state.IdPhotoB[0].url
          ? this.state.IdPhotoB[0].url
          : '', //身份证反面
      openAccountLicenceUrlLocal:
        this.state.openAccountLicenceUrls.length > 0 && this.state.openAccountLicenceUrls[0].url
          ? this.state.openAccountLicenceUrls[0].url
          : '', //*开户许可证照片
    };
    const merchanBusiness = this.state.merchanBusiness ? this.state.merchanBusiness : {};
    const mergedObj = Object.assign({}, params, merchanBusiness);
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...mergedObj,
      },
      url: `/ddql/merchant/add`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.setState(
            {
              current: this.state.current + 1,
            },
            () => {
              this.setState({
                dataDetails: res,
              });
            },
          );
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  contactPhones = (e) => {
    this.formRef.current.setFieldsValue({
      cardMobile: e.target.value,
      accMobile: e.target.value,
    });
  };

  MerchantNames = (e) => {
    this.formRef.current.setFieldsValue({
      storeName: e.target.value,
      businessName: e.target.value,
      accName: e.target.value,
    });
  };

  fullName = (e) => {
    this.formRef.current.setFieldsValue({
      cardName: e.target.value,
    });
  };

  addressa = (e) => {
    this.formRef.current.setFieldsValue({
      businessAddr: e.target.value,
    });
  };

  areaa = (e) => {
    this.formRef.current.setFieldsValue({
      businessProvince: e,
    });
  };

  render() {
    const { current, stadiumList = [], dataDetails = {} } = this.state;

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

    const steps = [
      {
        title: 'First',
        // content: (
        // ),
      },
      {
        title: 'Second',
        // content: (
        //   <div style={{ marginTop: 50 }}>
        //     <Form ref={this.formRefs} {...layout}>
        //       <XX />
        //     </Form>
        //   </div>
        // ),
      },
      {
        title: 'Last',
        // content: 'Last-content',
      },
    ];

    // const items = steps.map((item) => ({
    //   key: item.title,
    //   title: item.title,
    // }));

    console.log(current);

    return (
      <PageContainer
        header={{
          title: `新增商户在线进件`,
          breadcrumb: {
            itemRender: this.itemRender,
            routes,
          },
          extra: (
            <div>
              <Popconfirm
                title={
                  <>
                    <div>温馨提示</div>
                    <div>
                      <span style={{ color: '#ccc' }}>否要保存草稿</span>
                    </div>
                  </>
                }
                onConfirm={() => this.nextss(4)}
                okText="是"
                cancelText="否"
                onCancel={() => {
                  history.goBack();
                }}
              >
                <Button>返回</Button>
              </Popconfirm>
            </div>
          ),
        }}
      >
        <div style={{ backgroundColor: '#fff', padding: 24 }}>
          <div style={{ maxWidth: 1100, margin: '0 auto', marginTop: 20 }}>
            <Steps current={current}>
              <Steps.Step title="填写进件信息" />
              <Steps.Step title="确认进件信息" />
              <Steps.Step title="完成" />
            </Steps>
          </div>

          {/* <Steps current={current} items={items} /> */}

          <div className="steps-content">{steps[current].content}</div>
          <div className="steps-action">
            {current == 0 || current == 1 ? (
              <Form ref={this.formRef} {...layout} style={{ marginTop: 50 }}>
                <XX
                  refd={this.formRef}
                  IDImage={(e, ee, eee, eeee) => {
                    this.setState({
                      IdPhoto: e,
                      IdPhotoB: ee, //身份证反面照片
                      licenceUrls: eee, //营业执照照片
                      openAccountLicenceUrls: eeee, //开户许可证照片
                    });
                  }}
                  bankDetails={(e, ee) => {
                    this.setState({
                      bankNames: e,
                      bankBranchs: ee,
                    });
                  }}
                  Disables={this.state.Disables}
                  MerchantNames={(e) => this.MerchantNames(e)}
                  contactPhones={(e) => this.contactPhones(e)}
                  fullName={(e) => this.fullName(e)}
                  addressa={(e) => this.addressa(e)}
                  areaa={(e) => this.areaa(e)}
                  Disablesss={this.state.Disablesss}
                  // operationTypes={this.state.operationTypes}
                  // documentType={this.state.documentType}
                />
              </Form>
            ) : (
              ''
            )}

            {current == 0 && (
              <>
                <Row>
                  <Col span={5} offset={8}>
                    <Button
                      type="primary"
                      onClick={() => this.next()}
                      style={{ marginRight: 15, width: 120, height: 40, marginTop: 10 }}
                    >
                      下一步
                    </Button>
                    <Button
                      onClick={() => this.nextss(4)}
                      style={{ marginRight: 15, width: 120, height: 40, marginTop: 10 }}
                    >
                      保存草稿
                    </Button>
                  </Col>
                </Row>
              </>
            )}

            {current == 1 && (
              <>
                <Row>
                  <Col span={5} offset={8}>
                    <Button
                      type="primary"
                      onClick={() => this.submits(2)}
                      style={{ marginRight: 15, width: 120, height: 40, marginTop: 10 }}
                    >
                      提交
                    </Button>
                    <Button
                      style={{ marginRight: 15, width: 120, height: 40, marginTop: 10 }}
                      onClick={() => this.prev()}
                    >
                      上一步
                    </Button>
                  </Col>
                </Row>
              </>
            )}

            {current == 2 && (
              <>
                <Result
                  status={dataDetails?.msg !== '添加成功' ? 'error' : 'success'}
                  title={
                    dataDetails?.msg !== '添加成功'
                      ? dataDetails?.msg
                      : '进件申请提交成功，正在等待开通中'
                  }
                  subTitle={
                    dataDetails?.msg !== '添加成功'
                      ? dataDetails?.msg
                      : '预计一个工作日内有审批结果'
                  }
                />

                <div
                  style={{
                    backgroundColor: '#fafafa',
                    width: 600,
                    margin: '0 auto',
                    padding: '40px 20px',
                    fontSize: 14,
                  }}
                >
                  <div style={{ display: 'flex', marginBottom: 20 }}>
                    <div style={{ width: 140, textAlign: 'right', color: '#333' }}>商户名称：</div>
                    <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>{this.state.merchantBasic?.merchantName}</div>
                  </div>
                  <div style={{ display: 'flex', marginBottom: 20 }}>
                    <div style={{ width: 140, textAlign: 'right', color: '#333' }}>进件申请单编号：</div>
                    <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>
                      {dataDetails?.data?.applicationNo}
                      <a style={{ marginLeft: 15, color: '#1890ff' }} onClick={() => this.props.history.goBack()}>查看详情&gt;</a>
                    </div>
                  </div>
                  <div style={{ display: 'flex', marginBottom: 20 }}>
                    <div style={{ width: 140, textAlign: 'right', color: '#333' }}>提交时间：</div>
                    <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>{new Date().toLocaleString('zh-CN')}</div>
                  </div>
                  <div style={{ display: 'flex' }}>
                    <div style={{ width: 140, textAlign: 'right', color: '#333' }}>申请状态：</div>
                    <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>
                      {dataDetails?.data?.applicationStatus === 'REVIEWING' && (
                        <span>
                          <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} />
                          申请审核中
                        </span>
                      )}
                      {dataDetails?.data?.applicationStatus === 'REVIEW_BACK' && (
                        <span>
                          <CloseCircleOutlined style={{ color: 'red', marginRight: 5 }} />
                          申请已驳回
                        </span>
                      )}
                      {dataDetails?.data?.applicationStatus === 'AGREEMENT_SIGNING' && (
                        <span>
                          <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} />
                          协议待签署
                        </span>
                      )}
                      {dataDetails?.data?.applicationStatus === 'BUSINESS_OPENING' && (
                        <span>
                          <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} />
                          业务开通中
                        </span>
                      )}
                      {dataDetails?.data?.applicationStatus === 'COMPLETED' && (
                        <span>
                          <CheckCircleTwoTone twoToneColor="#52c41a" style={{ marginRight: 5 }} />
                          申请已完成（通过）
                        </span>
                      )}
                    </div>
                  </div>
                </div>
                {dataDetails?.data?.applicationStatus === 'REVIEW_BACK' && (
                  <div
                    style={{
                      width: 550,
                      margin: '0 auto',
                      textAlign: 'center',
                      marginTop: 30,
                      marginBottom: 50,
                    }}
                  >
                    <Button type="primary" onClick={() => this.props.history.goBack()}>
                      修改进件信息
                    </Button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </PageContainer>
    );
  }
}

export default connect()(NoticeNotice);
