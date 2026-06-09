import { history } from '@umijs/max';

export default (initialState: { permissions?: number[]; role?: string }) => {
  try {
    const ressd = initialState.permissions;
    return {
      home: ressd?.some((v) => [1].includes(v)), //数据总览
      shangjiaManagement: ressd?.some((v) => [85, 124, 154].includes(v)), //商家信息管理
      shangjiaManagementcheckInReview: ressd?.some((v) => [85].includes(v)), //商家信息管理
      shangjiaManagementindex: ressd?.some((v) => [124].includes(v)), //商家入驻审核
      shangquanMsg: ressd?.some((v) => [14].includes(v)), //商圈信息管理
      orderManagement: ressd?.some((v) => [101, 107].includes(v)), // 商品订单管理
      orderManagementindex: ressd?.some((v) => [101].includes(v)), // 商品订单管理+
      orderManagementRefundReview: ressd?.some((v) => [107].includes(v)), // 商品订单退款审核
      terminalEquipment: ressd?.some((v) => [20, 138].includes(v)), // 终端订单管理
      terminalEquipmentindex: ressd?.some((v) => [20].includes(v)), // 终端设备管理
      terminalEquipmentstorefrontRentalDisplay: ressd?.some((v) =>
        [138].includes(v),
      ), // 设备店位租用展示
      storefrontRentalManagement: ressd?.some((v) => [139].includes(v)), // 店位租用管理
      storefrontRentalManagementindex: ressd?.some((v) => [139].includes(v)), // 店位订单
      reconciliationManagement: ressd?.some((v) => [7].includes(v)), //财务管理
      reconciliationManagementindex: ressd?.some((v) => [7].includes(v)), //对账订单
      reconciliationManagementtransactionRecord: ressd?.some((v) =>
        [7].includes(v),
      ), //交易流水
      reconciliationManagementtransactionSummary: ressd?.some((v) =>
        [7].includes(v),
      ), //交易汇总
      reconciliationManagementtransactionDetails: ressd?.some((v) =>
        [7].includes(v),
      ), //交易详情
      reconciliationManagementsummaryOfSubLedger: ressd?.some((v) =>
        [7].includes(v),
      ), //分账汇总
      reconciliationManagementbillingDetails: ressd?.some((v) =>
        [7].includes(v),
      ), //分账明细
      merchantManagement: ressd?.some((v) => [8].includes(v)), //商户管理
      noticeNotice: ressd?.some((v) => [9].includes(v)), //公告通知
      userManagement: ressd?.some((v) => [10].includes(v)), //用户管理
      authorityManagement: ressd?.some((v) => [67, 73].includes(v)), //权限管理
      authorityManagementindex: ressd?.some((v) => [67].includes(v)), //管理员管理
      authorityManagementadministratorLog: ressd?.some((v) => [73].includes(v)), //管理员日志
      authorityManagementroleManagement: ressd?.some((v) =>
        [67, 73].includes(v),
      ), //角色管理
      systemSettings: ressd?.some((v) => [38, 57].includes(v)), //系统设置
      systemSettingsindex: ressd?.some((v) => [38].includes(v)), //关于我们
      systemSettingsclientSettings: ressd?.some((v) => [57].includes(v)), //用户端设置
      appVerstion: ressd?.some((v) => [13].includes(v)), //版本信息
    };
  } catch (e) {
    history.push('/user/login');
    return { permissions: [] };
  }
  // console.log(initialState,initialState.permissions);
  // 在这里按照初始化数据定义项目中的权限，统一管理
  // 参考文档 https://umijs.org/docs/max/access
  // const canSeeAdmin = !!(
  //   initialState && initialState.name !== 'dontHaveAccess'
  // );
  return {
    // canSeeAdmin,
  };
};
