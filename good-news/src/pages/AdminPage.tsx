import styled from "styled-components";
import AdminContentWrap from "../components/admin/AdminContentWrap";

// 관리자 관리 페이지
const AdminPage = () => {
  return (
    <StyledAdminPageWrapper>
      {/* 카드 컴포넌트 들어갈 자리 */}
      <StyledAdminCard>
        <AdminContentWrap />
      </StyledAdminCard>
    </StyledAdminPageWrapper>
  );
};

export default AdminPage;

const StyledAdminPageWrapper = styled.div`
  width: 100%;
  height: calc(100vh - 60px);
  background-color: lavender;
  display: flex;
  align-items: center;
  justify-content: center;
`;

// 임시 카드 컴포넌트
const StyledAdminCard = styled.div`
  width: 80%;
  height: 90%;
  background-color: #fff;
  padding: 50px;
`;
