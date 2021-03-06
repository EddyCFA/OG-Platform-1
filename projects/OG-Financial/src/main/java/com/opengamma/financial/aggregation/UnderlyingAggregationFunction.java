/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.aggregation;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import com.opengamma.core.position.Position;
import com.opengamma.core.position.impl.SimplePositionComparator;
import com.opengamma.core.security.Security;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.financial.security.FinancialSecurityVisitor;
import com.opengamma.financial.security.FinancialSecurityVisitorAdapter;
import com.opengamma.financial.security.fx.FXForwardSecurity;
import com.opengamma.financial.security.fx.NonDeliverableFXForwardSecurity;
import com.opengamma.financial.security.option.EquityBarrierOptionSecurity;
import com.opengamma.financial.security.option.EquityIndexOptionSecurity;
import com.opengamma.financial.security.option.EquityOptionSecurity;
import com.opengamma.financial.security.option.FXBarrierOptionSecurity;
import com.opengamma.financial.security.option.FXDigitalOptionSecurity;
import com.opengamma.financial.security.option.FXOptionSecurity;
import com.opengamma.financial.security.option.IRFutureOptionSecurity;
import com.opengamma.financial.security.option.NonDeliverableFXDigitalOptionSecurity;
import com.opengamma.financial.security.option.NonDeliverableFXOptionSecurity;
import com.opengamma.financial.security.option.SwaptionSecurity;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.id.ExternalScheme;
import com.opengamma.util.money.UnorderedCurrencyPair;

/**
 * Abstract aggregation function for bucketing equities and equity options by GICS code of the underlying
 */
public class UnderlyingAggregationFunction implements AggregationFunction<String> {
  private boolean _useAttributes;
  
  private ExternalScheme _preferredScheme;
  private SecuritySource _secSource;
  private Comparator<Position> _comparator = new SimplePositionComparator();
  
  private static final String NOT_APPLICABLE = "N/A";
  /* to make dep injection easier */
  public UnderlyingAggregationFunction(SecuritySource secSource, String preferredSchemeString) {
    this(secSource, ExternalScheme.of(preferredSchemeString));
  }
  
  public UnderlyingAggregationFunction(SecuritySource secSource, String preferredSchemeString, boolean useAttributes) {
    this(secSource, ExternalScheme.of(preferredSchemeString), useAttributes);
  }
  
  public UnderlyingAggregationFunction(SecuritySource secSource, ExternalScheme preferredScheme) {
    this(secSource, preferredScheme, false);
  }
  
  public UnderlyingAggregationFunction(SecuritySource secSource, ExternalScheme preferredScheme, boolean useAttributes) {
    _secSource = secSource;
    _preferredScheme = preferredScheme;
    _useAttributes = useAttributes;
  }
  
  private FinancialSecurityVisitor<String> _equityIndexOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitEquityIndexOptionSecurity(EquityIndexOptionSecurity security) {
      //Security underlying = _secSource.get(ExternalIdBundle.of(security.getUnderlyingId()));
      // we could use a historical time series source to look up the bundle at this point.
      String identifier = security.getUnderlyingId().getValue();
      return identifier != null ? identifier : NOT_APPLICABLE;
    }
  };
  
  private FinancialSecurityVisitor<String> _equityOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitEquityOptionSecurity(EquityOptionSecurity security) {
      Security underlying = _secSource.getSingle(ExternalIdBundle.of(security.getUnderlyingId()));
      if (underlying != null) {
        String identifier = underlying.getExternalIdBundle().getValue(_preferredScheme);
        return identifier != null ? identifier : NOT_APPLICABLE;
      } else {
        String identifier = security.getUnderlyingId() != null ? security.getUnderlyingId().getValue() : null;
        return identifier != null ? identifier : NOT_APPLICABLE;
      }
    }    
  };
  
  private FinancialSecurityVisitor<String> _equityBarrierOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitEquityBarrierOptionSecurity(EquityBarrierOptionSecurity security) {
      Security underlying = _secSource.getSingle(ExternalIdBundle.of(security.getUnderlyingId()));
      if (underlying != null) {
        String identifier = underlying.getExternalIdBundle().getValue(_preferredScheme);
        return identifier != null ? identifier : NOT_APPLICABLE;
      } else {
        String identifier = security.getUnderlyingId() != null ? security.getUnderlyingId().getValue() : null;
        return identifier != null ? identifier : NOT_APPLICABLE;
      }
    }
  };
  
  private FinancialSecurityVisitor<String> _fxOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitFXOptionSecurity(FXOptionSecurity fxOptionSecurity) {
      UnorderedCurrencyPair unorderedPair = UnorderedCurrencyPair.of(fxOptionSecurity.getCallCurrency(), fxOptionSecurity.getPutCurrency());
      return unorderedPair.getFirstCurrency() + "/" + unorderedPair.getSecondCurrency();
    }
  };
  
  private FinancialSecurityVisitor<String> _ndfFxOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitNonDeliverableFXOptionSecurity(NonDeliverableFXOptionSecurity fxOptionSecurity) {
      UnorderedCurrencyPair unorderedPair = UnorderedCurrencyPair.of(fxOptionSecurity.getCallCurrency(), fxOptionSecurity.getPutCurrency());
      return unorderedPair.getFirstCurrency() + "/" + unorderedPair.getSecondCurrency();
    }
  };
  
  private FinancialSecurityVisitor<String> _fxDigitalOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitFXDigitalOptionSecurity(FXDigitalOptionSecurity fxOptionSecurity) {
      UnorderedCurrencyPair unorderedPair = UnorderedCurrencyPair.of(fxOptionSecurity.getCallCurrency(), fxOptionSecurity.getPutCurrency());
      return unorderedPair.getFirstCurrency() + "/" + unorderedPair.getSecondCurrency();
    }
  };
  
  
  private FinancialSecurityVisitor<String> _ndfFxDigitalOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitNonDeliverableFXDigitalOptionSecurity(NonDeliverableFXDigitalOptionSecurity fxOptionSecurity) {
      UnorderedCurrencyPair unorderedPair = UnorderedCurrencyPair.of(fxOptionSecurity.getCallCurrency(), fxOptionSecurity.getPutCurrency());
      return unorderedPair.getFirstCurrency() + "/" + unorderedPair.getSecondCurrency();
    }
  };
  
  
  private FinancialSecurityVisitor<String> _fxBarrierOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitFXBarrierOptionSecurity(FXBarrierOptionSecurity fxBarrierOptionSecurity) {
      UnorderedCurrencyPair unorderedPair = UnorderedCurrencyPair.of(fxBarrierOptionSecurity.getCallCurrency(), fxBarrierOptionSecurity.getPutCurrency());
      return unorderedPair.getFirstCurrency() + "/" + unorderedPair.getSecondCurrency();
    }
  };
  
  private FinancialSecurityVisitor<String> _fxForwardSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitFXForwardSecurity(FXForwardSecurity fxForwardSecurity) {
      UnorderedCurrencyPair unorderedPair = UnorderedCurrencyPair.of(fxForwardSecurity.getPayCurrency(), fxForwardSecurity.getReceiveCurrency());
      return unorderedPair.getFirstCurrency() + "/" + unorderedPair.getSecondCurrency();
    }
  };
  
   
  private FinancialSecurityVisitor<String> _fxNdfForwardSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitNonDeliverableFXForwardSecurity(NonDeliverableFXForwardSecurity ndfFxForwardSecurity) {
      UnorderedCurrencyPair unorderedPair = UnorderedCurrencyPair.of(ndfFxForwardSecurity.getPayCurrency(), ndfFxForwardSecurity.getReceiveCurrency());
      return unorderedPair.getFirstCurrency() + "/" + unorderedPair.getSecondCurrency();
    }
  };
  
    
  private FinancialSecurityVisitor<String> _irFutureOptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    @Override
    public String visitIRFutureOptionSecurity(IRFutureOptionSecurity security) {
      Security underlying = _secSource.getSingle(ExternalIdBundle.of(security.getUnderlyingId()));
      String identifier = underlying.getExternalIdBundle().getValue(_preferredScheme);
      return identifier != null ? identifier : NOT_APPLICABLE;
    }
  };
  
  private FinancialSecurityVisitor<String> _swaptionSecurityVisitor = new FinancialSecurityVisitorAdapter<String>() {
    public String visitSwaptionSecurity(SwaptionSecurity security) {
      SwapSecurity underlying = (SwapSecurity) _secSource.getSingle(ExternalIdBundle.of(security.getUnderlyingId()));
      String name = underlying.getName();
      return (name != null && name.length() > 0) ? name : NOT_APPLICABLE;
    }    
  };

  @Override
  public String classifyPosition(Position position) {
    if (_useAttributes) {
      Map<String, String> attributes = position.getAttributes();
      if (attributes.containsKey(getName())) {
        return attributes.get(getName());
      } else {
        return NOT_APPLICABLE;
      } 
    } else {
      FinancialSecurityVisitor<String> visitorAdapter = FinancialSecurityVisitorAdapter.<String>builder()
                                                                                              .equityIndexOptionVisitor(_equityIndexOptionSecurityVisitor)
                                                                                              .equityOptionVisitor(_equityOptionSecurityVisitor)
                                                                                              .equityBarrierOptionVisitor(_equityBarrierOptionSecurityVisitor)
                                                                                              .fxForwardVisitor(_fxForwardSecurityVisitor)
                                                                                              .nonDeliverableFxForwardVisitor(_fxNdfForwardSecurityVisitor)
                                                                                              .fxOptionVisitor(_fxOptionSecurityVisitor)
                                                                                              .nonDeliverableFxOptionVisitor(_ndfFxOptionSecurityVisitor)
                                                                                              .fxDigitalOptionVisitor(_fxDigitalOptionSecurityVisitor)
                                                                                              .fxNonDeliverableDigitalOptionVisitor(_ndfFxDigitalOptionSecurityVisitor)
                                                                                              .fxBarrierOptionVisitor(_fxBarrierOptionSecurityVisitor)
                                                                                              .irfutureOptionVisitor(_irFutureOptionSecurityVisitor)
                                                                                              .swaptionVisitor(_swaptionSecurityVisitor)
                                                                                              .create();
      FinancialSecurity security = (FinancialSecurity) position.getSecurityLink().resolve(_secSource);
      try {
        String classification = security.accept(visitorAdapter);
        return classification == null ? NOT_APPLICABLE : classification;
      } catch (UnsupportedOperationException uoe) {
        return NOT_APPLICABLE;
      }
      
    }
  }

  @Override
  public String getName() {
    return "Underlying";
  }

  @Override
  public Collection<String> getRequiredEntries() {
    return Collections.emptyList();
  }

  @Override
  public int compare(String o1, String o2) {
    if (o1.equals(NOT_APPLICABLE)) {
      if (o2.equals(NOT_APPLICABLE)) {
        return 0;
      }
      return 1;
    } else if (o2.equals(NOT_APPLICABLE)) {
      return -1;
    }
    return o1.compareTo(o2);
  }

  @Override
  public Comparator<Position> getPositionComparator() {
    return _comparator;
  }

}
