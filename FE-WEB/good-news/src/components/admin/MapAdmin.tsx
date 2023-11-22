// 관리 페이지에서 쓰이는 지도
import styled from "styled-components";

interface MapAdminProps {
  className?: string;
}

const MapAdmin = ({ className }: MapAdminProps) => {
  return (
    <StyledMapWrapper className={className}>지도 들어가용</StyledMapWrapper>
  );
};

export default MapAdmin;

const StyledMapWrapper = styled.div`
  width: 100%;
  background-color: blue;
  border-radius: 16px;
`;
