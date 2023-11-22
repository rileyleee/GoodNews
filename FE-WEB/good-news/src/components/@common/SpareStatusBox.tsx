// 상태 + 아이콘 컴포넌트 (여유혼잡)
import styled from "styled-components";
import { LuAnnoyed, LuFrown, LuSmile } from "react-icons/lu";

interface SpareStatusProps {
  keyword?: SpareKeyword;
  className?: string;
}

type SpareKeyword = "Spare" | "Confusion" | "Undefined";
type SpareKeywordStyle = {
  text: string;
  Icon: React.ComponentType;
  color: string;
};
const SpareKeywordStyles: Record<SpareKeyword, SpareKeywordStyle> = {
  Spare: {
    text: "여유",
    Icon: LuSmile,
    color: "#34B476",
  },
  Confusion: {
    text: "혼잡",
    Icon: LuFrown,
    color: "#EF476F",
  },
  Undefined: {
    text: "미확인",
    Icon: LuAnnoyed,
    color: "#C5C5C5",
  },
};

const StyledSpareStatusBox = styled.div<SpareStatusProps>`
  color: ${(props) => SpareKeywordStyles[props.keyword || "Undefined"].color};
  font-weight: bold; // 폰트 설정됐으면 해당 폰트로 변경해주기! (font-family)

  display: flex;
  align-items: center;
  *:last-child {
    margin-left: 0.25rem;
  }
`;

const SpareStatusBox = ({ keyword, className }: SpareStatusProps) => {
  const { text, Icon } = SpareKeywordStyles[keyword || "Undefined"];

  return (
    <StyledSpareStatusBox keyword={keyword} className={className}>
      <span>{text}</span>
      <Icon />
    </StyledSpareStatusBox>
  );
};

export default SpareStatusBox;
