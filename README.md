Specyfikacja formatu zapisu portfela

Format jest tekstowy i liniowy. Kolejnosc rekordow:
- HEADER
- CASH
- RESERVED
- (POZYCJE i LOTY)

Rekordy:
HEADER|PORTFOLIO|<portfolioId>
CASH|<amount>|<currencyCode>
RESERVED|<amount>|<currencyCode>
POSITION|<assetType>|<ticker>|<totalQuantity>
LOT|<yyyy-MM-dd>|<quantity>|<unitPrice>

Uwagi:
- assetType to enum: SHARE, COMMODITY, CURRENCY
- Suma ilosci w rekordach LOT musi rownac sie totalQuantity z POSITION
- Waluty CASH i RESERVED musza byc takie same

Przyklad:
HEADER|PORTFOLIO|P1
CASH|10500.50|USD
RESERVED|0.00|USD
POSITION|SHARE|AAPL|15
LOT|2023-05-10|10|150.00
LOT|2023-06-12|5|155.00
